package com.ktbweek4.community.post.controller;

import com.ktbweek4.community.common.ApiResponse;
import com.ktbweek4.community.common.CommonCode;
import com.ktbweek4.community.common.SliceResponse;
import com.ktbweek4.community.post.dto.*;
import com.ktbweek4.community.post.service.PostService;
import com.ktbweek4.community.user.dto.CustomUserDetails;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    // 게시글 생성 (Spring Security 자동 인증)
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<PostResponseDTO>> createPost(
            @RequestPart("post") PostRequestDTO postRequestDTO,
            @RequestPart(value = "images", required = false) List<MultipartFile> images,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) throws Exception {
        PostResponseDTO savedPost = postService.createPost(postRequestDTO, images, userDetails);
        return ApiResponse.success(CommonCode.POST_CREATED, savedPost).toResponseEntity();
    }

    // 게시글 수정 (Spring Security 자동 인증)
    @PatchMapping(value = "/{postId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE) 
    public ResponseEntity<ApiResponse<PostResponseDTO>> updatePost(
            @PathVariable Long postId,
            @RequestPart(value = "post", required = false) PostUpdateRequestDTO updateDTO,
            @RequestPart(value = "newImages", required = false) List<MultipartFile> newImages,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) throws Exception {
        if (updateDTO == null) updateDTO = new PostUpdateRequestDTO();
        PostResponseDTO updated = postService.updatePost(postId, updateDTO, newImages, userDetails);
        return ApiResponse.success(CommonCode.POST_UPDATED, updated).toResponseEntity();
    }

    // 게시글 삭제 (Spring Security 자동 인증)
    @DeleteMapping("/{postId}")
    public ResponseEntity<ApiResponse<Void>> deletePost(
            @PathVariable Long postId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) throws Exception {
        postService.deletePost(postId, userDetails);
        return ApiResponse.<Void>success(CommonCode.POST_DELETED).toResponseEntity();
    }

    // 게시글 상세 조회
    @GetMapping("/{postId}")
    public ResponseEntity<ApiResponse<PostDetailResponseDTO>> getPost(
            @PathVariable Long postId,
            HttpServletRequest request
    ) throws Exception {
        PostDetailResponseDTO getPost = postService.getPost(postId, request);
        return ApiResponse.success(CommonCode.POST_VIEW, getPost).toResponseEntity();
    }

    // 게시글 목록 조회
    /**
     * 인피니티 스크롤 목록
     * 예: GET /api/v1/posts?size=20&cursor=123
     * cursor가 없으면 최신부터 시작
     */
    @GetMapping
    public ResponseEntity<ApiResponse<SliceResponse<PostListItemDTO>>> getPosts(
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(required = false) Long cursor
    ) {
        var slice = postService.getPostsSlice(cursor, Math.min(size, 5));
        return ApiResponse.success(CommonCode.SUCCESS, slice).toResponseEntity();
    }
}
