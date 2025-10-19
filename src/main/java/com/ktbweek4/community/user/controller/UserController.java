package com.ktbweek4.community.user.controller;

import com.ktbweek4.community.common.ApiResponse;
import com.ktbweek4.community.common.CommonCode;
import com.ktbweek4.community.user.dto.CustomUserDetails;
import com.ktbweek4.community.user.dto.UserRequestDTO;
import com.ktbweek4.community.user.dto.UserResponseDTO;
import com.ktbweek4.community.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    
    private final UserService userService;

    // 회원가입
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<UserResponseDTO>> signup(@RequestBody UserRequestDTO request) {
        UserResponseDTO saved = userService.create(request);
        return ApiResponse.success(CommonCode.USER_CREATED, saved).toResponseEntity();
    }

    // 로그인한 사용자 정보 조회 (Spring Security가 자동으로 인증 정보 주입)
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponseDTO>> me(@AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails == null) {
            return ApiResponse.<UserResponseDTO>error(CommonCode.UNAUTHORIZED).toResponseEntity();
        }

        UserResponseDTO response = UserResponseDTO.builder()
                .userId(userDetails.getUserId())
                .email(userDetails.getEmail())
                .nickname(userDetails.getNickname())
                .build();

        return ApiResponse.success(CommonCode.SUCCESS, response).toResponseEntity();
    }
}
