package com.ktbweek4.community.post.dto;


import com.ktbweek4.community.post.entity.PostEntity;
import com.ktbweek4.community.post.entity.PostImageEntity;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Data
@Builder
public class PostDetailResponseDTO {

    private Long postId;
    private String title;
    private String content;
    private Long authorId;
    private String authorNickname; // 작성자 닉네임

    private List<ImageInfo> images;

    private Long viewCount;
    private Long likesCount; // 좋아요 개수
    private Boolean likedByMe; // 로그인 사용자가 누른 상태인지 여부

    private Integer commentsCount;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Data
    @Builder
    public static class ImageInfo {
        private Long imageId;
        private String url;
        private Byte orderIndex;
        private Boolean isPrimary;
    }

    public static PostDetailResponseDTO of(PostEntity post, boolean likedByMe, long likesCount, int commentsCount) {
        List<PostDetailResponseDTO.ImageInfo> imageInfos = post.getPostImages().stream()
                .sorted(Comparator.comparing(PostImageEntity::getOrderIndex,
                        Comparator.nullsLast(Byte::compare)))
                .map(img -> PostDetailResponseDTO.ImageInfo.builder()
                        .imageId(img.getPostImageId())
                        .url(img.getPostImageUrl())
                        .orderIndex(img.getOrderIndex())
                        .isPrimary(img.getIsPrimary())
                        .build())
                .toList();

        return PostDetailResponseDTO.builder()
                .postId(post.getPostId())
                .title(post.getTitle())
                .content(post.getContent())
                .authorId(post.getAuthor().getUserId())
                .authorNickname(post.getAuthor().getNickname())
                .images(imageInfos)
                .viewCount(post.getViewCount())
                .likesCount(likesCount)
                .likedByMe(likedByMe)
                .commentsCount(commentsCount)
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .build();
    }
}
