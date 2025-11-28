// src/main/java/com/ktbweek4/community/post/like/service/PostLikeService.java
package com.ktbweek4.community.post.like.service;

import com.ktbweek4.community.post.dto.PostLikeResponseDTO;
import com.ktbweek4.community.post.entity.PostEntity;
import com.ktbweek4.community.post.like.entity.PostLike;
import com.ktbweek4.community.post.like.repository.PostLikeRepository;
import com.ktbweek4.community.post.repository.PostRepository;
import com.ktbweek4.community.user.dto.CustomUserDetails;
import com.ktbweek4.community.user.entity.User;
import com.ktbweek4.community.user.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class PostLikeService {

    private final PostRepository postRepository;
    private final PostLikeRepository postLikeRepository;
    private final UserService userService;

    /** 좋아요 */
    public PostLikeResponseDTO like(Long postId, CustomUserDetails userDetails) {
        if (userDetails == null) throw new IllegalArgumentException("인증이 필요합니다.");

        PostEntity post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다."));
        User user = userService.findByIdOrThrow(userDetails.getUserId());

        // 이미 좋아요라면 idempotent 반환
        if (postLikeRepository.existsByPostAndUser(post, user)) {
            long count = postLikeRepository.countByPost(post);
            return PostLikeResponseDTO.of(true, (int) count);
        }

        // 삽입 시도 (동시성으로 인한 UNIQUE 충돌은 무시)
        try {
            postLikeRepository.save(PostLike.of(post, user));
            postRepository.incrementLikesCount(post.getPostId());
        } catch (DataIntegrityViolationException ignore) {
            // 누군가가 거의 동시에 눌러서 UNIQUE 제약에 걸린 경우 → 이미 좋아요 상태로 간주
        }

        long count = postLikeRepository.countByPost(post);
        return PostLikeResponseDTO.of(true, (int) count);
    }

    /** 좋아요 취소 */
    public PostLikeResponseDTO unlike(Long postId, CustomUserDetails userDetails) {
        if (userDetails == null) throw new IllegalArgumentException("인증이 필요합니다.");

        PostEntity post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다."));
        User user = userService.findByIdOrThrow(userDetails.getUserId());

        int deleted = postLikeRepository.deleteByPostAndUser(post, user);
        if (deleted > 0) {
            postRepository.decrementLikesCountSafe(post.getPostId());
        }

        long count = postLikeRepository.countByPost(post);
        return PostLikeResponseDTO.of(false, (int) count);
    }
}