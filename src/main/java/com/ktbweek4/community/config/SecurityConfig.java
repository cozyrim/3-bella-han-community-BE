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
            "/files/**"
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // CSRF 보호 임시 비활성화 (프론트엔드 연동 문제 해결용)
                .csrf(csrf -> csrf.disable())
                
                // CORS 설정: 다른 도메인(localhost:5500)에서의 요청 허용
                // credentials(쿠키) 포함 요청 가능하도록 설정
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // 세션 관리: 필요할 때만 세션 생성 (로그인 시에만 생성됨)
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))

                // URL 별 접근 권한 설정
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(PUBLIC_URLS).permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/posts/**").permitAll()
                        .anyRequest().authenticated()
                )

                // 폼 로그인 비활성화 (REST API는 JSON으로 로그인 처리)
                .formLogin(form -> form.disable())
                
                // HTTP Basic 인증 비활성화 (세션 방식만 사용)
                .httpBasic(basic -> basic.disable());

        return http.build();
    }

    // 비밀번호 암호화 도구 (BCrypt 알고리즘 사용)
    // 회원가입 시 비밀번호 해싱, 로그인 시 비밀번호 검증에 사용됨
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 인증 관리자: 로그인 요청 시 자동으로 인증 처리
    // UserDetailsService + PasswordEncoder를 사용하여 DB 조회 및 비밀번호 검증 수행
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    // 인증 제공자: 실제 인증 로직 수행
    // 1) UserDetailsService로 DB에서 사용자 조회
    // 2) PasswordEncoder로 비밀번호 검증
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);  // 사용자 조회 서비스 연결
        provider.setPasswordEncoder(passwordEncoder());      // 비밀번호 암호화 도구 연결
        return provider;
    }

    // CORS 설정: 프론트엔드 도메인에서 백엔드 API 호출 허용
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        // 허용할 프론트엔드 도메인 (Live Server, 로컬 파일)
        config.setAllowedOrigins(List.of("http://localhost:5500", "http://127.0.0.1:5500", "file://"));
        // 허용할 HTTP 메서드
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        // 모든 헤더 허용 (Content-Type, Authorization 등)
        config.setAllowedHeaders(List.of("*"));
        // 쿠키(JSESSIONID) 전송 허용 (세션 방식 필수)
        config.setAllowCredentials(true);
        // Preflight 요청 캐시 시간 (초)
        config.setMaxAge(3600L);

        // CORS 설정을 Spring에 등록하는 코드
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);  // 모든 경로에 적용
        return source;
    }
}
