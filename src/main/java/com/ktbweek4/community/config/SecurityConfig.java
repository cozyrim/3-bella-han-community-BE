package com.ktbweek4.community.config;

import com.ktbweek4.community.auth.jwt.JwtAuthenticationFilter;
import com.ktbweek4.community.auth.jwt.JwtTokenProvider;
import com.ktbweek4.community.user.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;
    //private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtTokenProvider jwtTokenProvider;
//    private static final String[] PUBLIC_URLS = {
//            "/actuator/health", "/actuator/health/**", "/actuator/info",
//            "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html",
//            "/", "/terms", "/privacy", "/css/**", "/js/**", "/images/**",
//            "/api/v1/auth/login", "/api/v1/users/signup",
//            "/api/v1/users/check-email", "/api/v1/users/check-nickname",
//            "/v1/users/check-email", "/v1/users/check-nickname",
//            "/v1/auth/login", "/v1/users/signup",
//            "/files/**", "/", "/favicon.ico", "/error", "/api/health", "/actuator/**"
//    };
    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtTokenProvider, userDetailsService);
    }
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth
                        // 1) 회원가입 / 로그인 / 인증 관련은 가장 먼저 무조건 허용!
                        .requestMatchers(
                                "/api/v1/users/signup",
                                "/api/v1/users/check-email",
                                "/api/v1/users/check-nickname",
                                "/api/v1/auth/login",
                                "/api/v1/auth/refresh",
                                "/api/v1/auth/logout"
                        ).permitAll()

                        // 2) OPTIONS 전체 허용
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // 3) Swagger & 정적 리소스
                        // 3) 정적 리소스
                        .requestMatchers(
                                "/", "/favicon.ico", "/error",
                                "/css/**", "/js/**", "/images/**",
                                "/terms", "/privacy",
                                "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html"
                        ).permitAll()


                        // 4) 나머지는 인증 필요
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable());

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOriginPatterns(List.of(
                "http://localhost:3000",
                "http://127.0.0.1:3000",
                "http://3.36.180.192",
                "http://3.36.180.192:3000",
                "http://community-a-feast-of-n.e.kr",
                "http://community-a-feast-of-n.e.kr:3000"
        ));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);
        config.setExposedHeaders(List.of("Authorization"));
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}