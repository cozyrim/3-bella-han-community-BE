package com.ktbweek4.community.comment.dto;

import com.ktbweek4.community.comment.entity.Comment;
import com.ktbweek4.community.post.entity.PostEntity;
import com.ktbweek4.community.user.entity.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;



public record CommentCreateRequestDTO(

        @NotBlank(message = "댓글 내용을 입력해주세요.")
        @Size(max = 255, message = "댓글은 최대 255자까지 입력할 수 있습니다.")
        String content
 ) {


    public static Comment toEntity(User author, PostEntity postEntity, String content) {
        return Comment.builder()
                .author(author)
                .post(postEntity)
                .content(content)
                .build();

    }
}




