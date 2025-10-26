package com.ktbweek4.community.comment.controller;

import com.ktbweek4.community.comment.dto.CommentCreateRequestDTO;
import com.ktbweek4.community.comment.dto.CommentResponseDTO;
import com.ktbweek4.community.comment.entity.Comment;
import com.ktbweek4.community.comment.service.CommentService;
import com.ktbweek4.community.common.ApiResponse;
import com.ktbweek4.community.common.CommonCode;
import com.ktbweek4.community.user.dto.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.attribute.UserPrincipal;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/comments")
public class CommentController {
    private final CommentService commentService;

    // 게시글 생성
    @PostMapping()
    public ResponseEntity<ApiResponse<CommentResponseDTO>> addComment(
            @RequestBody CommentCreateRequestDTO commentCreateRequestDTO,
            @AuthenticationPrincipal CustomUserDetails customUserDetails)
    throws Exception {
        CommentResponseDTO savedComment = commentService.createComment(commentCreateRequestDTO, customUserDetails);
        return ApiResponse.success(CommonCode.COMMENT_CREATED, savedComment).toResponseEntity();


    }
}
