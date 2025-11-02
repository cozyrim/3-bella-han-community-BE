package com.ktbweek4.community.auth;

import com.ktbweek4.community.common.ApiResponse;
import com.ktbweek4.community.common.CommonCode;
import com.ktbweek4.community.user.dto.LoginRequestDTO;
import com.ktbweek4.community.user.dto.UserResponseDTO;
import com.ktbweek4.community.user.entity.User;
import com.ktbweek4.community.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<UserResponseDTO>> login(
            @RequestBody LoginRequestDTO request,
            HttpServletRequest httpRequest) {

        User user = userRepository.findByEmail(request.getEmail()).orElse(null);
        if (user == null || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return ApiResponse.<UserResponseDTO>error(CommonCode.USER_INVALID_PASSWORD).toResponseEntity();
        }

        HttpSession session = httpRequest.getSession(true);
        session.setAttribute(SessionConst.LOGIN_USER,
                new SessionUser(user.getUserId(), user.getEmail(), user.getNickname()));
        httpRequest.changeSessionId(); // 세션 고정 방지

        return ApiResponse.success(CommonCode.LOGIN_SUCCESS, UserResponseDTO.of(user)).toResponseEntity();
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) session.invalidate();
        return ApiResponse.<Void>success(CommonCode.LOGOUT_SUCCESS).toResponseEntity();
    }
}