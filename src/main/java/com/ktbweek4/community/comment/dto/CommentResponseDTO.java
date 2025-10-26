package com.ktbweek4.community.comment.dto;

import com.ktbweek4.community.comment.entity.Comment;

import java.time.LocalDateTime;

public record CommentResponseDTO (
        Long commentId,
        Long postId,
        Long authorId,
        String authorNickname,
        String content,
        Integer likesCount,
        boolean mine,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static CommentResponseDTO of(Comment c, Long currentUserId) {
        return new CommentResponseDTO(
                c.getCommentId(),
                c.getPostId(),
                c.getAuthorId(),
                c.getAuthor().getNickname(),
                c.getContent(),
                c.getLikesCount(),
                currentUserId != null && currentUserId.equals(c.getAuthorId()),
                c.getCreatedAt(),
                c.getUpdatedAt()
        );
    }
}

