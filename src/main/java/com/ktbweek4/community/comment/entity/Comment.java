package com.ktbweek4.community.comment.entity;

import com.ktbweek4.community.common.BaseTimeEntity;
import com.ktbweek4.community.post.entity.PostEntity;
import com.ktbweek4.community.user.entity.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;


@Entity
@Getter @ Setter
@Table(name = "comments")
public class Comment extends BaseTimeEntity {
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
    private PostEntity post;

    // 읽기 전용 칼럼 - 쿼리 최적화용
    @Column(name = "user_id", insertable = false, updatable = false)
    private Long authorId;

    @Column(name = "post_id", insertable = false, updatable = false)
    private Long postId;

    @Column(name = "content", length = 255, nullable = false)
    private String content;

    @Column(name = "likes_count", columnDefinition = "INT DEFAULT 0", nullable = false)
    private Integer likesCount = 0;

    @Column(name = "created_at", columnDefinition = "DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6)", insertable = false,updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", columnDefinition = "DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6)")
    private LocalDateTime updatedAt;

    protected Comment() {}

    @Builder
    public Comment(User author, PostEntity post, String content) {
        this.author = author;
        this.post = post;
        this.content = content;
        this.likesCount = 0;
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
