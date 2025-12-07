// com.ktbweek4.community.post.dto.PostListItemDTO
package com.ktbweek4.community.post.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class PostListItemDTO {
    private Long postId;
    private String title;
    private String content;
    private Long authorId;
    private long viewCount;
    private Integer likesCount;
    private Long commentsCount;
    private String authorNickname;
    private String primaryImageUrl; // 대표 이미지(없을 수 있음)
    private LocalDateTime createdAt;
}