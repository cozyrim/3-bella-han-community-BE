// src/main/java/com/ktbweek4/community/post/dto/PostLikeResponseDTO.java
package com.ktbweek4.community.post.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PostLikeResponseDTO {
    private boolean liked;
    private int likesCount;

    public static PostLikeResponseDTO of(boolean liked, int likesCount) {
        return new PostLikeResponseDTO(liked, likesCount);
    }
}