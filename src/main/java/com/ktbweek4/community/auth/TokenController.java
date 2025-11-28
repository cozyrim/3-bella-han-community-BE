// src/main/java/com/ktbweek4/community/auth/TokenController.java
package com.ktbweek4.community.auth;

import com.ktbweek4.community.auth.entity.RefreshToken;
import com.ktbweek4.community.auth.repository.RefreshTokenRepository;
import com.ktbweek4.community.auth.util.CookieUtil;
import com.ktbweek4.community.auth.util.TokenHash;
import com.ktbweek4.community.auth.jwt.JwtTokenProvider;
import com.ktbweek4.community.common.ApiResponse;
import com.ktbweek4.community.common.CommonCode;
import com.ktbweek4.community.user.dto.CustomUserDetails;
import com.ktbweek4.community.user.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.time.Instant;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class TokenController {

    private final JwtTokenProvider jwt;
    private final RefreshTokenRepository repo;
    private final CustomUserDetailsService userDetailsService;

    @Value("${security.cookie.domain}") private String cookieDomain;
    @Value("${security.cookie.path}") private String cookiePath;
    @Value("${security.cookie.secure:false}") private boolean cookieSecure;
    @Value("${security.cookie.same-site:Lax}") private String cookieSameSite;
    @Value("${jwt.refresh-expiration}") private long refreshValidityMs;

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<Map<String, String>>> refresh(
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        String refresh = CookieUtil.getCookie(request, AuthController.REFRESH_COOKIE);
        if (refresh == null) {
            return ApiResponse.<Map<String, String>>error(CommonCode.UNAUTHORIZED).toResponseEntity();
        }

        // 1) 서명/만료 검증
        var jws = jwt.parseJws(refresh); // 유효하지 않으면 예외
        if (jws.getBody().getExpiration().toInstant().isBefore(Instant.now())) {
            return ApiResponse.<Map<String, String>>error(CommonCode.UNAUTHORIZED).toResponseEntity();
        }

        String jti = jws.getBody().getId();
        String email = jws.getBody().getSubject();

        // 2) DB 조회(해시/또는 jti) + revoked=false 확인
        var hash = TokenHash.sha256(refresh);
        var stored = repo.findByTokenHash(hash).orElse(null);
        if (stored == null || stored.isRevoked() || stored.getExpiresAt().isBefore(Instant.now())) {
            return ApiResponse.<Map<String, String>>error(CommonCode.UNAUTHORIZED).toResponseEntity();
        }

        // 3) 회전: 기존 레코드 revoke 후 새 Refresh/Access 발급
        stored.setRevoked(true);
        repo.save(stored);

        var user = (CustomUserDetails) userDetailsService.loadUserByUsername(email);

        String newAccess = jwt.generateAccessToken(user);
        String newJti = jwt.newJti();
        String newRefresh = jwt.generateRefreshToken(user, newJti);

        var rt = new RefreshToken();
        rt.setUserId(user.getUser().getUserId());
        rt.setJti(newJti);
        rt.setTokenHash(TokenHash.sha256(newRefresh));
        rt.setExpiresAt(jwt.getExpiryInstant(newRefresh));
        rt.setRevoked(false);
        repo.save(rt);

        // 4) 새 쿠키로 교체
        CookieUtil.addHttpOnlyCookie(
                response, AuthController.REFRESH_COOKIE, newRefresh,
                cookieDomain, cookiePath, cookieSecure, cookieSameSite,
                (int)(refreshValidityMs / 1000)
        );

        return ApiResponse
                .success(CommonCode.REFRESH_SUCCESS, Map.of(
                        "accessToken", newAccess
                ))
                .toResponseEntityWithHeaders(Map.of("Authorization", "Bearer " + newAccess));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(HttpServletRequest request, HttpServletResponse response) {
        String refresh = CookieUtil.getCookie(request, AuthController.REFRESH_COOKIE);
        if (refresh != null) {
            repo.findByTokenHash(TokenHash.sha256(refresh))
                    .ifPresent(rt -> { rt.setRevoked(true); repo.save(rt); });
            CookieUtil.deleteCookie(response, AuthController.REFRESH_COOKIE,
                    cookieDomain, cookiePath, cookieSecure, cookieSameSite);
        }
        return ApiResponse.<Void>success(CommonCode.LOGOUT_SUCCESS).toResponseEntity();
    }
}