package com.ktbweek4.community.common;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 400: 잘못된 요청
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleBadRequest(IllegalArgumentException e) {
        return ApiResponse.<Void>error(CommonCode.BAD_REQUEST, e.getMessage()).toResponseEntity();
    }

    // 403: 권한 없음
    @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Void>> handleAccessDenied(Exception e) {
        return ApiResponse.<Void>error(CommonCode.FORBIDDEN, "접근 권한이 없습니다.").toResponseEntity();
    }

    // 401: 인증 실패 (토큰 없음 / invalid token 등)
    @ExceptionHandler(org.springframework.security.core.AuthenticationException.class)
    public ResponseEntity<ApiResponse<Void>> handleAuth(Exception e) {
        return ApiResponse.<Void>error(CommonCode.UNAUTHORIZED, "인증이 필요합니다.").toResponseEntity();
    }

    // 500: 그 외 모든 예외
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleServer(Exception e) {
        return ApiResponse.<Void>error(CommonCode.INTERNAL_SERVER_ERROR).toResponseEntity();
    }

}