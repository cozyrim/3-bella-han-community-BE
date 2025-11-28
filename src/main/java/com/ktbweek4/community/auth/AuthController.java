// src/main/java/com/ktbweek4/community/auth/AuthController.java
package com.ktbweek4.community.auth;

import com.ktbweek4.community.auth.entity.RefreshToken;
import com.ktbweek4.community.auth.repository.RefreshTokenRepository;
import com.ktbweek4.community.auth.util.CookieUtil;
import com.ktbweek4.community.auth.util.TokenHash;
import com.ktbweek4.community.auth.jwt.JwtTokenProvider;
import com.ktbweek4.community.common.ApiResponse;
import com.ktbweek4.community.common.CommonCode;
import com.ktbweek4.community.user.dto.CustomUserDetails;
import com.ktbweek4.community.user.dto.LoginRequestDTO;
import com.ktbweek4.community.user.dto.UserResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;
import java.time.Instant;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${security.cookie.domain}") private String cookieDomain;
    @Value("${security.cookie.path}") private String cookiePath;
    @Value("${security.cookie.secure:false}") private boolean cookieSecure;
    @Value("${security.cookie.same-site:Lax}") private String cookieSameSite;
    // refresh Max-Age는 yml의 refresh-expiration(ms) / 1000로 맞춰도 됨
    @Value("${jwt.refresh-expiration}") private long refreshValidityMs;

    public static final String REFRESH_COOKIE = "REFRESH_TOKEN";

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(
            @RequestBody LoginRequestDTO request,
            HttpServletResponse response
    ) {
        var auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        var user = (CustomUserDetails) auth.getPrincipal();

        // 1) Access
        String access = jwtTokenProvider.generateAccessToken(user);

        // 2) Refresh (jti 생성 → 토큰 생성 → 해시 저장)
        String jti = jwtTokenProvider.newJti();
        String refresh = jwtTokenProvider.generateRefreshToken(user, jti);

        var rt = new RefreshToken();
        rt.setUserId(user.getUser().getUserId());
        rt.setJti(jti);
        rt.setTokenHash(TokenHash.sha256(refresh));
        rt.setExpiresAt(jwtTokenProvider.getExpiryInstant(refresh));
        rt.setRevoked(false);
        refreshTokenRepository.save(rt);

        // 3) HttpOnly 쿠키에 저장
        CookieUtil.addHttpOnlyCookie(
                response, REFRESH_COOKIE, refresh,
                cookieDomain, cookiePath, cookieSecure, cookieSameSite,
                (int)(refreshValidityMs / 1000)
        );

        var body = new LoginResponse(UserResponseDTO.of(user.getUser()), access);
        return ApiResponse
                .success(CommonCode.LOGIN_SUCCESS, body)
                .toResponseEntityWithHeaders(Map.of("Authorization", "Bearer " + access));
    }

    public record LoginResponse(UserResponseDTO user, String accessToken) {}
}