package com.ktbweek4.community.comment.service;


import com.ktbweek4.community.comment.dto.CommentCreateRequestDTO;
import com.ktbweek4.community.comment.dto.CommentResponseDTO;
import com.ktbweek4.community.comment.entity.Comment;
import com.ktbweek4.community.comment.repository.CommentRepository;
import com.ktbweek4.community.post.entity.PostEntity;
import com.ktbweek4.community.post.repository.PostRepository;
import com.ktbweek4.community.user.dto.CustomUserDetails;
import com.ktbweek4.community.user.entity.User;
import com.ktbweek4.community.user.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.ktbweek4.community.comment.entity.QComment.comment;

@Transactional
@RequiredArgsConstructor
@Service
public class CommentService {
    private final CommentRepository commentRepository;
    private final UserService userService;
    private final PostRepository postRepository;



    // 댓글 생성
    public CommentResponseDTO createComment(CommentCreateRequestDTO commentCreateRequestDTO,
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

        PostEntity post = postRepository.findById(commentCreateRequestDTO.postId())
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 존재하지 않습니다."));

            Comment comment = CommentCreateRequestDTO.toEntity(loginUser, post, commentCreateRequestDTO.content());
            Comment saved = commentRepository.save((comment));

        return CommentResponseDTO.of(saved, loginUser.getUserId());
    }

}
