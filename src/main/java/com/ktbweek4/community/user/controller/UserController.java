package com.ktbweek4.community.user.controller;

import com.ktbweek4.community.common.ApiResponse;
import com.ktbweek4.community.common.CommonCode;
import com.ktbweek4.community.user.dto.UserRequestDTO;
import com.ktbweek4.community.user.dto.UserResponseDTO;
import com.ktbweek4.community.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<UserResponseDTO>> signup(@RequestBody UserRequestDTO request) {
        UserResponseDTO saved = userService.create(request);
        return ApiResponse.success(CommonCode.USER_CREATED, saved).toResponseEntity(); // 201 + 바디
    }
}
