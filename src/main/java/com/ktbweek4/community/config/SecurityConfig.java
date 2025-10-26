package com.ktbweek4.community.config;

import com.ktbweek4.community.user.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    // 사용자 정보를 DB에서 조회하는 서비스 (로그인 시 자동 호출됨)
    private final CustomUserDetailsService userDetailsService;

    // 인증 없이 접근 가능한 공개 API 목록
    private static final String[] PUBLIC_URLS = {
            "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html",
            "/api/v1/auth/login", "/api/v1/users/signup", 
            "/files/**", "/api/v1/debug/csrf",
            "/api/v1/comments"
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // CSRF 보호 설정: 쿠키 기반 토큰 사용, 로그인/회원가입은 CSRF 검증 제외
                .csrf(csrf -> csrf
                        //.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                        .csrfTokenRepository(cookieCsrfTokenRepository())
                        .ignoringRequestMatchers("/api/v1/auth/login", "/api/v1/users/signup")
                )

                // CORS 설정: 다른 도메인(localhost:5500)에서의 요청 허용
                // credentials(쿠키) 포함 요청 가능하도록 설정
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // URL 별 접근 권한 설정
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(PUBLIC_URLS).permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/posts/**").permitAll()
                        .anyRequest().authenticated()
                )

                // 세션 관리: 필요할 때만 세션 생성 (로그인 시에만 생성됨)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED) // 세션 생성 정책
                        .sessionFixation().changeSessionId()                      // 세션 고정 방지
                        .maximumSessions(1)                                       // 동시 로그인 제한
                        .maxSessionsPreventsLogin(false)
                )

                // 폼/베이직 인증은 비활성화 (JSON 로그인 사용)
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable())

                // 로그아웃 시 세션/컨텍스트/쿠키 정리
                .logout(logout -> logout
                        .logoutUrl("/api/v1/auth/logout")
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .deleteCookies("JSESSIONID", "XSRF-TOKEN")
                );

        return http.build();
    }

    @Bean
    public CookieCsrfTokenRepository cookieCsrfTokenRepository() {
        CookieCsrfTokenRepository repository = CookieCsrfTokenRepository.withHttpOnlyFalse();
        repository.setCookiePath("/");
        // SameSite 속성으로 추가 CSRF 보호 (Spring Security 6.1+)
        repository.setCookieCustomizer(cookie -> cookie
                .sameSite("Lax")  // Strict 또는 Lax 권장
                .secure(false)     // HTTPS에서만 전송 (프로덕션)
        );
        return repository;
    }


    // CORS 설정: 프론트엔드 도메인에서 백엔드 API 호출 허용
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        // 허용할 프론트엔드 도메인 (Live Server, 로컬 파일)
        config.setAllowedOriginPatterns(List.of("http://localhost:*","http://127.0.0.1:*"));
        // 허용할 HTTP 메서드
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        // 모든 헤더 허용
        config.setAllowedHeaders(List.of("*"));
        // 쿠키(JSESSIONID) 전송 허용 (세션 방식 필수)
        config.setAllowCredentials(true);
        // 응답 헤더 노출 (CSRF 토큰 등)
        config.setExposedHeaders(List.of("Content-Type","X-XSRF-TOKEN","Accept","Authorization"));

        // Preflight 요청 캐시 시간 (초)
        config.setMaxAge(3600L);

        // CORS 설정을 Spring에 등록하는 코드
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);  // 모든 경로에 적용
        return source;
    }

    // 인증 관리자: 로그인 요청 시 자동으로 인증 처리
    // UserDetailsService + PasswordEncoder를 사용하여 DB 조회 및 비밀번호 검증 수행
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }


    // 비밀번호 암호화 도구 (BCrypt 알고리즘 사용)
    // 회원가입 시 비밀번호 해싱, 로그인 시 비밀번호 검증에 사용됨
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }



}
