package com.ktbweek4.community.auth.jwt;

import com.ktbweek4.community.user.dto.CustomUserDetails;
import com.ktbweek4.community.user.service.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailsService userDetailsService;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String uri = request.getRequestURI();
        System.out.println("🔥 JWT shouldNotFilter check — URI: " + uri);

        return uri.startsWith("/api/v1/users/signup") ||
                uri.startsWith("/api/v1/users/check-email") ||
                uri.startsWith("/api/v1/users/check-nickname") ||
                uri.startsWith("/api/v1/auth/login") ||
                uri.startsWith("/api/v1/auth/refresh") ||
                uri.startsWith("/api/v1/auth/logout") ||
                uri.startsWith("/swagger") ||
                uri.startsWith("/v3/api-docs") ||
                uri.startsWith("/css/") ||
                uri.startsWith("/js/") ||
                uri.startsWith("/images/") ||
                uri.equals("/") ||
                uri.equals("/favicon.ico");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        System.out.println(">>> RAW URI = " + request.getRequestURI());
        System.out.println(">>> RAW URL = " + request.getRequestURL());
        System.out.println("🚨 doFilterInternal — 요청 URI: " + request.getRequestURI());
        String token = resolveToken(request);

        if (token != null && jwtTokenProvider.validateToken(token)) {
            String email = jwtTokenProvider.getUsername(token);
            CustomUserDetails userDetails = (CustomUserDetails) userDetailsService.loadUserByUsername(email);

            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContextHolder.getContext().setAuthentication(authToken);
        }

        filterChain.doFilter(request, response);
    }

    private String resolveToken(HttpServletRequest request) {
        String bearer = request.getHeader("Authorization");
        if (bearer != null && bearer.startsWith("Bearer ")) {
            return bearer.substring(7);
        }
        return null;
    }
}