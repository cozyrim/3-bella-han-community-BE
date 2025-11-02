package com.ktbweek4.community.auth;

import com.ktbweek4.community.auth.jwt.JwtTokenProvider;
import com.ktbweek4.community.common.ApiResponse;
import com.ktbweek4.community.common.CommonCode;
import com.ktbweek4.community.user.dto.CustomUserDetails;
import com.ktbweek4.community.user.dto.LoginRequestDTO;
import com.ktbweek4.community.user.dto.UserResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@RequestBody LoginRequestDTO request) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        String token = jwtTokenProvider.generateToken(userDetails);

        UserResponseDTO userDto = UserResponseDTO.of(userDetails.getUser());
        LoginResponse loginResponse = new LoginResponse(userDto, token);

        return ApiResponse
                .success(CommonCode.LOGIN_SUCCESS, loginResponse) // T = LoginResponse
                .toResponseEntityWithHeaders(Map.of("Authorization", "Bearer " + token));
    }

    public record LoginResponse(UserResponseDTO user, String accessToken) {}
}