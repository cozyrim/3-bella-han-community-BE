package com.ktbweek4.community.comment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CommentUpdateRequestDTO (
        @NotBlank(message = "수정할 댓글 내용을 입력하세요.")
        @Size(max = 255, message = "댓글은 최대 255자까지 입력할 수 있습니다.")
        String content
        ) { }

