package com.ktbweek4.community.post.service;

import com.ktbweek4.community.comment.dto.CommentCreateRequestDTO;
import com.ktbweek4.community.comment.dto.CommentResponseDTO;
import com.ktbweek4.community.comment.dto.CommentUpdateRequestDTO;
import com.ktbweek4.community.comment.entity.Comment;
import com.ktbweek4.community.comment.repository.CommentRepository;
import com.ktbweek4.community.common.SliceResponse;
import com.ktbweek4.community.file.LocalFileStorage;
import com.ktbweek4.community.post.dto.*;
import com.ktbweek4.community.post.entity.PostEntity;
import com.ktbweek4.community.post.entity.PostImageEntity;
import com.ktbweek4.community.post.like.repository.PostLikeRepository;
import com.ktbweek4.community.post.repository.PostRepository;
import com.ktbweek4.community.user.dto.CustomUserDetails;
import com.ktbweek4.community.user.entity.User;
import com.ktbweek4.community.user.service.UserService;
import jakarta.annotation.Nullable;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class PostService {

    private final PostLikeRepository postLikeRepository;
    private final PostRepository postRepository;
    private final LocalFileStorage fileStorage;
    private final EntityManager em;
    private final UserService userService;

    private final CommentRepository commentRepository;

    @PersistenceContext
    EntityManager entityManager;


    /** 생성: 제목/내용 + 이미지 업로드 (Spring Security 자동 인증) */
    public PostResponseDTO createPost(PostRequestDTO dto,
                                      List<MultipartFile> images,
                                      CustomUserDetails userDetails) throws Exception {

        // 인증 정보 확인 (디버깅)
        if (userDetails == null) {
            System.out.println("PostService.createPost - userDetails가 null입니다!");
            throw new IllegalArgumentException("인증 정보가 없습니다.");
        }

        System.out.println("PostService.createPost 호출 - userDetails: " + userDetails.getEmail());
        System.out.println("사용자 ID: " + userDetails.getUserId());

        // 로그인 사용자 조회 (Spring Security가 자동으로 인증 확인)
        User loginUser = userService.findByIdOrThrow(userDetails.getUserId());
        System.out.println("사용자 조회 성공: " + loginUser.getEmail());
        System.out.println("사용자 닉네임: " + loginUser.getNickname());

        // 2) 게시글 저장
        PostEntity post = PostEntity.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .author(loginUser)
                .build();
        post = postRepository.save(post);

        // 3) 이미지 저장 (있으면)
        byte order = 0;
        
        // MultipartFile 이미지 처리 (FormData 업로드)
        if (images != null && !images.isEmpty()) {
            for (MultipartFile img : images) {
                if (img == null || img.isEmpty()) continue;

                var stored = fileStorage.save(img); // 로컬 저장 + 공개 URL
                PostImageEntity image = PostImageEntity.builder()
                        .postImageUrl(stored.publicUrl())
                        .orderIndex(order)
                        .isPrimary(order == 0) // 첫 번째 이미지를 대표로
                        .build();

                post.addImage(image);
                order++;
            }
        }
        
        // S3 이미지 URL 리스트 처리 (Lambda 업로드 후 JSON 전송)
        if (dto.getImageUrls() != null && !dto.getImageUrls().isEmpty()) {
            for (String imageUrl : dto.getImageUrls()) {
                if (imageUrl == null || imageUrl.isBlank()) continue;
                
                PostImageEntity image = PostImageEntity.builder()
                        .postImageUrl(imageUrl) // 이미 S3에 업로드된 URL 사용
                        .orderIndex(order)
                        .isPrimary(order == 0) // 첫 번째 이미지를 대표로
                        .build();
                
                post.addImage(image);
                order++;
            }
        }
        em.flush();
        // 4) 응답
        return PostResponseDTO.of(post);
    }

    /** 수정: 제목/내용, 이미지 추가/삭제, 대표 이미지 설정 (Spring Security 자동 인증) */
    public PostResponseDTO updatePost(Long postId,
                                      PostUpdateRequestDTO dto,
                                      List<MultipartFile> newImages,
                                      CustomUserDetails userDetails) throws Exception {

        // 로그인 사용자 조회 (Spring Security가 자동으로 인증 확인)
        User loginUser = userService.findByIdOrThrow(userDetails.getUserId());

        // 2) 게시글 조회
        PostEntity post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        // 3) 작성자 확인
        if (!post.getAuthor().getUserId().equals(loginUser.getUserId())) {
            throw new IllegalStateException("게시글 작성자만 수정할 수 있습니다.");
        }

        // dto null 안전 처리 (이미지만 보낼 수 있으므로)
        if (dto == null) dto = new PostUpdateRequestDTO();

        // 4) 제목/내용 수정
        if (dto.getTitle() != null)  post.setTitle(dto.getTitle());
        if (dto.getContent() != null) post.setContent(dto.getContent());

        // 5) 이미지 삭제
        if (dto.getRemoveImageIds() != null && !dto.getRemoveImageIds().isEmpty()) {
            Map<Long, PostImageEntity> current = post.getPostImages()
                    .stream().collect(Collectors.toMap(PostImageEntity::getPostImageId, it -> it));

            for (Long imgId : dto.getRemoveImageIds()) {
                PostImageEntity image = current.get(imgId);
                if (image != null) {
                    try { fileStorage.deleteByUrl(image.getPostImageUrl()); } catch (Exception ignore) {}
                    post.removeImage(image); // orphanRemoval=true로 DB행 삭제
                }
            }
        }

        // 6) 새 이미지 추가
        byte nextOrder = (byte) post.getPostImages().size();
        if (newImages != null && !newImages.isEmpty()) {
            for (MultipartFile img : newImages) {
                if (img == null || img.isEmpty()) continue;

                var stored = fileStorage.save(img);

                PostImageEntity image = PostImageEntity.builder()
                        .postImageUrl(stored.publicUrl())
                        .orderIndex(nextOrder++)
                        .isPrimary(false)
                        .build();

                post.addImage(image);
            }
        }

        // 7) 대표 이미지 설정
        if (dto.getPrimaryImageId() != null) {
            Long primaryId = dto.getPrimaryImageId();
            for (PostImageEntity img : post.getPostImages()) {
                img.setIsPrimary(Objects.equals(img.getPostImageId(), primaryId));
            }
        } else {
            boolean hasPrimary = post.getPostImages().stream().anyMatch(PostImageEntity::getIsPrimary);
            if (!hasPrimary && !post.getPostImages().isEmpty()) {
                post.getPostImages().get(0).setIsPrimary(true);
            }
        }

        // 8) orderIndex 재정렬
        byte idx = 0;
        for (PostImageEntity img : post.getPostImages().stream()
                .sorted(Comparator.comparing(PostImageEntity::getOrderIndex, Comparator.nullsLast(Byte::compare)))
                .collect(Collectors.toList())) {
            img.setOrderIndex(idx++);
        }
        em.flush();
        // 9) 응답
        return PostResponseDTO.of(post);
    }

    /** 삭제: 작성자만 삭제 가능 (Spring Security 자동 인증) */
    public void deletePost(Long postId, CustomUserDetails userDetails) {
        // 로그인 사용자 조회 (Spring Security가 자동으로 인증 확인)
        User loginUser = userService.findByIdOrThrow(userDetails.getUserId());

        // 게시글 조회
        PostEntity post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        // 삭제할 게시글의 작성자와 현재 로그인한 사람이 일치하는지 확인
        if (!post.getAuthor().getUserId().equals(loginUser.getUserId())) {
            throw new IllegalStateException("게시글 작성자만 삭제할 수 있습니다.");
        }

        postRepository.deleteById(postId);
    }

    public PostDetailResponseDTO getPost(Long postId, @Nullable Long currentUserId) {
        // 게시글 상세 조회는 공개 (인증 불필요)
        PostEntity post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        boolean likedByMe = false;
        if (currentUserId != null) {
            likedByMe = postLikeRepository.existsByPost_PostIdAndUser_UserId(postId, currentUserId);
        }

        long likesCount = postLikeRepository.countByPost_PostId(postId);
        int commentsCount = commentRepository.countByPost_PostId(postId);
        return PostDetailResponseDTO.of(post, likedByMe, likesCount, commentsCount);
    }

    public SliceResponse<PostListItemDTO> getPostsSlice(Long cursor, int size) {
        // size+1을 가져와서 hasNext 판정하는 패턴도 있음.
        List<PostListItemDTO> items = postRepository.fetchSliceByCursor(cursor, size + 1);

        boolean hasNext = false;
        Long nextCursor = null;

        if (items.size() > size) {
            hasNext = true;
            var last = items.remove(items.size() - 1); // 초과분 제거
            // nextCursor는 현재 리스트의 마지막 postId로 (역정렬 기준이므로 가장 작은 id)
        }

        if (!items.isEmpty()) {
            nextCursor = items.get(items.size() - 1).getPostId();
        }

        return new SliceResponse<>(items, hasNext, nextCursor);
    }



    // 댓글 생성
    public CommentResponseDTO createComment(Long postId,
                                            CommentCreateRequestDTO commentCreateRequestDTO,
                                            CustomUserDetails userDetails) throws Exception {
        // 인증 정보 확인
        if (userDetails == null) {
            System.out.println("PostService.createPost - userDetails가 null입니다!");
            throw new IllegalArgumentException("인증 정보가 없습니다.");
        }
        System.out.println("PostService.createPost 호출 - userDetails: " + userDetails.getEmail());
        System.out.println("사용자 ID: " + userDetails.getUserId());

        // 로그인 사용자 조회
        User loginUser = userService.findByIdOrThrow(userDetails.getUserId());
        System.out.println("사용자 조회 성공: " + loginUser.getEmail());
        System.out.println("사용자 닉네임: " + loginUser.getNickname());

        PostEntity post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 존재하지 않습니다."));

        Comment comment = CommentCreateRequestDTO.toEntity(loginUser, post, commentCreateRequestDTO.content());
        Comment saved = commentRepository.save((comment));

        return CommentResponseDTO.of(saved, loginUser.getUserId());
    }

    // 댓글 수정
    public CommentResponseDTO updateComment(Long postId,
                                            Long commentId,
                                            CommentUpdateRequestDTO commentUpdateRequestDTO,
                                            CustomUserDetails userDetails) throws Exception {

        // 로그인 사용자 조회 (Spring Security가 자동으로 인증 확인)
        User loginUser = userService.findByIdOrThrow(userDetails.getUserId());

        // 댓글 존재 조회
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다."));

        if (!comment.getPost().getPostId().equals(postId)) {
            throw new IllegalArgumentException("게시글과 댓글이 일치하지 않습니다.");
        }
        if (!comment.getAuthor().getUserId().equals(loginUser.getUserId())) {
            throw new IllegalArgumentException("작성자만 수정할 수 있습니다.");
        }

        comment.setContent(commentUpdateRequestDTO.content());
        Comment saved = commentRepository.save(comment);
        return CommentResponseDTO.of(saved, loginUser.getUserId());

    }
    // 댓글 삭제
    public void deleteComment(Long postId, Long commentId, CustomUserDetails userDetails) throws Exception {
        // 로그인 사용자 조회 (Spring Security가 자동으로 인증 확인)
        User loginUser = userService.findByIdOrThrow(userDetails.getUserId());

        // 댓글 존재 조회
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다."));

        if (!comment.getPost().getPostId().equals(postId)) {
            throw new IllegalArgumentException("게시글과 댓글이 일치하지 않습니다.");
        }
        if (!comment.getAuthor().getUserId().equals(loginUser.getUserId())) {
            throw new IllegalArgumentException("작성자만 삭제할 수 있습니다.");
        }
        commentRepository.deleteById(comment.getCommentId());
    }
    // 댓글 목록 조회 (댓글 인덱스, 게시글 번호, 댓글 내용, 순서(시간차 순))
    public List<CommentResponseDTO> getCommentList(Long postId, @Nullable CustomUserDetails user) throws Exception {
        var post = entityManager.createQuery( // Post를 조회할 때 댓글 + 작성자까지 한 번의 쿼리로 다 가져옴
                        "select distinct p " +
                                "from PostEntity p " +
                                "left join fetch p.comments c " + // PostEntity 한 개를 조회할 때, 그 게시글에 연결된 댓글들도 한 번에 가져옴
                                "left join fetch c.author a " + // 댓글의 작성자도 함께 가져옴, Comment와 연결된 User엔티티
                                "where p.postId = :postId", PostEntity.class)
                .setParameter("postId", postId)
                .getSingleResult(); // 하나의 게시글에 속한 댓글들

        Long currentUserId = (user == null) ? null : user.getUserId();

        return post.getComments().stream()
                .map(c -> CommentResponseDTO.of(c, currentUserId))
                .toList();
    }

    // 조회수
    public void increaseView(Long postId) {
        int updated = postRepository.incrementViewCount(postId);
        if (updated == 0) {
            throw new IllegalArgumentException("게시글을 찾을 수 없습니다.");
        }
    }
}