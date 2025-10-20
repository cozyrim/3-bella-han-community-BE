package com.ktbweek4.community.auth;

import com.ktbweek4.community.common.ApiResponse;
import com.ktbweek4.community.common.CommonCode;
import com.ktbweek4.community.user.dto.CustomUserDetails;
import com.ktbweek4.community.user.dto.LoginRequestDTO;
import com.ktbweek4.community.user.dto.UserResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;

    // 로그인: Spring Security 자동 인증 사용
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<UserResponseDTO>> login(@RequestBody LoginRequestDTO request, HttpServletRequest httpRequest) {
        try {
            // 인증 시도 (DB 조회 + 비밀번호 검증 자동 수행)
            Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );

            // SecurityContext에 인증 정보 저장
            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(auth);
            SecurityContextHolder.setContext(context);

            // JSESSIONID 생성: Tomcat이 세션 ID를 자동으로 생성하고 쿠키로 브라우저에 전송
            HttpSession session = httpRequest.getSession(true);  // true = 세션 없으면 새로 생성

            // SecurityContext를 세션에 저장 (이후 요청 시 자동으로 인증 상태 복원)
            session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, context);

            // 세션 고정 공격 방지: 로그인 성공 시 세션 ID를 새로 발급 (내용은 유지)
            httpRequest.changeSessionId();

            // 디버깅 로그 (임시)
            System.out.println("로그인 성공 - 세션 생성됨: " + session.getId());
            System.out.println("인증 객체 저장됨: " + auth.getName());

            // 응답 생성
            CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
            UserResponseDTO response = UserResponseDTO.builder()
                    .userId(userDetails.getUserId())
                    .email(userDetails.getEmail())
                    .nickname(userDetails.getNickname())
                    .build();

            return ApiResponse.success(CommonCode.LOGIN_SUCCESS, response).toResponseEntity();

        } catch (BadCredentialsException e) {
            return ApiResponse.<UserResponseDTO>error(CommonCode.USER_INVALID_PASSWORD).toResponseEntity();
        } catch (Exception e) {
            return ApiResponse.<UserResponseDTO>error(CommonCode.UNAUTHORIZED).toResponseEntity();
        }
    }

    // 로그아웃: 세션 무효화
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return ApiResponse.<Void>success(CommonCode.LOGOUT_SUCCESS).toResponseEntity();
    }
}
