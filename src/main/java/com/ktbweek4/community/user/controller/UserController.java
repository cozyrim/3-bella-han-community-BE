package com.ktbweek4.community.user.controller;

import com.ktbweek4.community.common.ApiResponse;
import com.ktbweek4.community.common.CommonCode;
import com.ktbweek4.community.user.dto.CustomUserDetails;
import com.ktbweek4.community.user.dto.UserRequestDTO;
import com.ktbweek4.community.user.dto.UserResponseDTO;
import com.ktbweek4.community.user.dto.UserSignupForm;
import com.ktbweek4.community.user.entity.User;
import com.ktbweek4.community.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import com.ktbweek4.community.user.dto.UserSignupForm;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    
    private final UserService userService;

    // 회원가입 (JSON)
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<UserResponseDTO>> signup(@RequestBody UserRequestDTO request) {
        UserResponseDTO saved = userService.create(request);
        return ApiResponse.success(CommonCode.USER_CREATED, saved).toResponseEntity();
    }
    
    // 회원가입 (FormData - 프로필 사진 포함)
    @PostMapping(value = "/signup", consumes = "multipart/form-data")
    public ResponseEntity<ApiResponse<UserResponseDTO>> signupWithProfile(@Valid @ModelAttribute UserSignupForm form) {

        
        UserResponseDTO saved = userService.createWithProfile(
                new UserRequestDTO(form.getEmail(), form.getPassword(), form.getNickname()),
                form.getProfileImage()
        );

        return ApiResponse.success(CommonCode.USER_CREATED, saved).toResponseEntity();
    }

    // 이메일 중복 검사
    @GetMapping("/check-email")
    public ResponseEntity<ApiResponse<Boolean>> checkEmail(@RequestParam String email) {
        boolean exists = userService.existsByEmail(email);
        return ApiResponse.success(CommonCode.SUCCESS, exists).toResponseEntity();
    }
    
    // 닉네임 중복 검사
    @GetMapping("/check-nickname")
    public ResponseEntity<ApiResponse<Boolean>> checkNickname(@RequestParam String nickname) {
        boolean exists = userService.existsByNickname(nickname);
        return ApiResponse.success(CommonCode.SUCCESS, exists).toResponseEntity();
    }

    // 로그인한 사용자 정보 조회 (Spring Security가 자동으로 인증 정보 주입)
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponseDTO>> me(@AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails == null) {
            return ApiResponse.<UserResponseDTO>error(CommonCode.UNAUTHORIZED).toResponseEntity();
        }

        User user = userService.findByIdOrThrow(userDetails.getUserId());

        return ApiResponse.success(CommonCode.SUCCESS, UserResponseDTO.of(user)).toResponseEntity();
    }
}
