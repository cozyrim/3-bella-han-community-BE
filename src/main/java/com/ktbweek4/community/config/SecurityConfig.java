package com.ktbweek4.community.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // REST API 형태이므로 CSRF 비활성화
                .csrf(csrf -> csrf.disable())

                // Swagger, 회원가입 등 공개 URL 설정
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html"
                        ).permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/users/signup").permitAll()
                        .anyRequest().authenticated()
                )

                // 기본 HTTP Basic 인증 사용 (테스트용)
                .httpBasic(Customizer.withDefaults());

//                // 세션 미사용 시(토큰 인증 구조로 변경 시) 아래 활성화
//                .sessionManagement(sm -> sm.sessionCreationPolicy(
//                        org.springframework.security.config.http.SessionCreationPolicy.STATELESS));

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}