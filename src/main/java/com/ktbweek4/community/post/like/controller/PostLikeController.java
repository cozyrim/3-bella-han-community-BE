// src/main/java/com/ktbweek4/community/post/like/controller/PostLikeController.java
package com.ktbweek4.community.post.like.controller;

import com.ktbweek4.community.common.ApiResponse;
import com.ktbweek4.community.common.CommonCode;
import com.ktbweek4.community.post.dto.PostLikeResponseDTO;
import com.ktbweek4.community.post.like.service.PostLikeService;
import com.ktbweek4.community.user.dto.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/posts")
public class PostLikeController {

    private final PostLikeService postLikeService;

    /** 좋아요 */
    @PostMapping("/{postId}/likes")
    public ResponseEntity<ApiResponse<PostLikeResponseDTO>> like(
            @PathVariable Long postId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        PostLikeResponseDTO dto = postLikeService.like(postId, userDetails);
        return ApiResponse.success(CommonCode.SUCCESS, dto).toResponseEntity();
    }

    /** 좋아요 취소 */
    @DeleteMapping("/{postId}/likes")
    public ResponseEntity<ApiResponse<PostLikeResponseDTO>> unlike(
            @PathVariable Long postId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        PostLikeResponseDTO dto = postLikeService.unlike(postId, userDetails);
        return ApiResponse.success(CommonCode.SUCCESS, dto).toResponseEntity();
    }
}