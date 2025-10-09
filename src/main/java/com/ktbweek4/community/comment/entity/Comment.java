package com.ktbweek4.community.comment.entity;

import com.ktbweek4.community.post.entity.Post;
import com.ktbweek4.community.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter @ Setter
@Table(name = "Comment")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long commentId;

    // comment n : 1 user
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User author;

    // comment n : 1 post
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    // 읽기 전용 칼럼 - 쿼리 최적화용
    @Column(name = "user_id", insertable = false, updatable = false)
    private Long authorId;

    @Column(name = "post_id", insertable = false, updatable = false)
    private Long postId;

    @Column(name = "content", length = 255)
    private String content;

    @Column(name = "likes_count", columnDefinition = "INT DEFAULT 0")
    private Integer likesCount = 0;

    @Column(name = "created_at", columnDefinition = "DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6)", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", columnDefinition = "DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6)")
    private LocalDateTime updatedAt;

    protected Comment() {}

    public Comment(Long userId, Long postId, String content) {
        this.authorId = userId;
        this.postId = postId;
        this.content = content;
        this.likesCount = 0;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
