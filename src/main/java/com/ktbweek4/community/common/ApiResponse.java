package com.ktbweek4.community.common;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private final boolean success;
    private final String code;
    private final String message;
    private final T data;

    @JsonIgnore
    private final HttpStatus httpStatus;

    @Builder
    private ApiResponse(boolean success, String code, String message, T data, HttpStatus httpStatus) {
        this.success = success;
        this.code = code;
        this.message = message;
        this.data = data;
        this.httpStatus = (httpStatus != null) ? httpStatus : HttpStatus.OK;
    }

    /* 성공 */
    public static <T> ApiResponse<T> success(CommonCode code, T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .code(code.getCode())
                .message(code.getMessage())
                .data(data)
                .httpStatus(code.httpStatus())
                .build();
    }

    public static ApiResponse<Void> success(CommonCode code) {
        return ApiResponse.<Void>builder()
                .success(true)
                .code(code.getCode())
                .message(code.getMessage())
                .httpStatus(code.httpStatus())
                .build();
    }

    /* 실패 */
    public static ApiResponse<Void> error(CommonCode code) {
        return ApiResponse.<Void>builder()
                .success(false)
                .code(code.getCode())
                .message(code.getMessage())
                .httpStatus(code.httpStatus())
                .build();
    }

    public static ApiResponse<Void> error(CommonCode code, String customMessage) {
        return ApiResponse.<Void>builder()
                .success(false)
                .code(code.getCode())
                .message(customMessage)
                .httpStatus(code.httpStatus())
                .build();
    }

    /* ResponseEntity 변환 */
    public ResponseEntity<ApiResponse<T>> toResponseEntity() {
        return ResponseEntity.status(this.httpStatus).body(this);
    }
}