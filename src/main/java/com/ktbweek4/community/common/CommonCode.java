package com.ktbweek4.community.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CommonCode {

    /* ====== 공통 성공 코드 ====== */
    SUCCESS("C200", "요청이 성공적으로 처리되었습니다.", HttpStatus.OK),
    CREATED("C201", "리소스가 성공적으로 생성되었습니다.", HttpStatus.CREATED),
    UPDATED("C204", "리소스가 성공적으로 수정되었습니다.", HttpStatus.OK),
    DELETED("C205", "리소스가 성공적으로 삭제되었습니다.", HttpStatus.OK),

    /* ====== 공통 에러 코드 ====== */
    BAD_REQUEST("E400", "잘못된 요청입니다.", HttpStatus.BAD_REQUEST),
    UNAUTHORIZED("E401", "인증이 필요합니다.", HttpStatus.UNAUTHORIZED),
    FORBIDDEN("E403", "접근 권한이 없습니다.", HttpStatus.FORBIDDEN),
    NOT_FOUND("E404", "리소스를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    CONFLICT("E409", "데이터 충돌이 발생했습니다.", HttpStatus.CONFLICT),
    INTERNAL_SERVER_ERROR("E500", "서버 내부 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),

    /* ====== User 관련 코드 ====== */
    USER_CREATED("U201", "회원가입이 완료되었습니다.", HttpStatus.CREATED),
    USER_UPDATED("U204", "회원 정보가 수정되었습니다.", HttpStatus.OK),
    USER_DELETED("U205", "회원 탈퇴가 완료되었습니다.", HttpStatus.OK),
    USER_ALREADY_EXISTS("U409", "이미 존재하는 회원입니다.", HttpStatus.CONFLICT),
    USER_NOT_FOUND("U404", "해당 회원을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    USER_INVALID_PASSWORD("U400", "비밀번호가 올바르지 않습니다.", HttpStatus.BAD_REQUEST),
    USER_UNAUTHORIZED("U401", "로그인이 필요합니다.", HttpStatus.UNAUTHORIZED),

    /* ====== Post 관련 코드 ====== */
    POST_CREATED("P201", "게시글이 성공적으로 작성되었습니다.", HttpStatus.CREATED),
    POST_UPDATED("P204", "게시글이 수정되었습니다.", HttpStatus.OK),
    POST_DELETED("P205", "게시글이 삭제되었습니다.", HttpStatus.OK),
    POST_NOT_FOUND("P404", "게시글을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    POST_FORBIDDEN("P403", "게시글 작성자가 아닙니다.", HttpStatus.FORBIDDEN),
    POST_VIEW("P206", "게시글 조회 성공했습니다.", HttpStatus.OK),


    /* ====== Comment 관련 코드 ====== */
    COMMENT_CREATED("CM201", "댓글이 등록되었습니다.", HttpStatus.CREATED),
    COMMENT_UPDATED("CM204", "댓글이 수정되었습니다.", HttpStatus.OK),
    COMMENT_DELETED("CM205", "댓글이 삭제되었습니다.", HttpStatus.OK),
    COMMENT_NOT_FOUND("CM404", "댓글을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),

    /* ====== Like 관련 코드 ====== */
    LIKE_ADDED("L201", "좋아요가 추가되었습니다.", HttpStatus.CREATED),
    LIKE_REMOVED("L205", "좋아요가 취소되었습니다.", HttpStatus.OK),
    LIKE_ALREADY_EXISTS("L409", "이미 좋아요를 누른 상태입니다.", HttpStatus.CONFLICT),

    /* ====== Image 관련 코드 ====== */
    IMAGE_UPLOADED("I201", "이미지가 업로드되었습니다.", HttpStatus.CREATED),
    IMAGE_DELETED("I205", "이미지가 삭제되었습니다.", HttpStatus.OK),
    IMAGE_INVALID_FORMAT("I400", "지원하지 않는 이미지 형식입니다.", HttpStatus.BAD_REQUEST),

    /* ====== Auth 관련 코드 ====== */
    TOKEN_EXPIRED("A401", "토큰이 만료되었습니다.", HttpStatus.UNAUTHORIZED),
    TOKEN_INVALID("A403", "유효하지 않은 토큰입니다.", HttpStatus.FORBIDDEN),
    LOGIN_SUCCESS("A200", "로그인에 성공했습니다.", HttpStatus.OK),
    LOGOUT_SUCCESS("A205", "로그아웃에 성공했습니다.", HttpStatus.OK);

    private final String code;
    private final String message;
    private final HttpStatus httpStatus;

    public HttpStatus httpStatus() { return httpStatus; }
}