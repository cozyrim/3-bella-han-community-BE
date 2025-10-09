package com.ktbweek4.community.post.entity;

import com.ktbweek4.community.comment.entity.Comment;
import com.ktbweek4.community.user.entity.PostImage;
import com.ktbweek4.community.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @ Setter
@Table(name = "Post")
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // auto_increment
    @Column(name = "post_id")
    private Long postId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User author;


    // 읽기용: 같은 칼럼을 중복 매핑 (쓰기 금지)
    @Column(name = "user_id", insertable = false, updatable = false)
    private Long authorId;

    // post 1 : N comment (글 삭제 시 댓글도 함께 제거)
    @OneToMany(mappedBy = "post", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    // post 1 : N postImage (연결/순서 관리 같이 함)
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostImage> postImages = new ArrayList<>();

    // post 1 : 1 postviewCount
    @OneToOne(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private PostViewCount viewCount;




    @Column(name = "title", length = 45)
    private String title;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "created_at", columnDefinition = "DATETIME(6)", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", columnDefinition = "DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6)")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    @Column(name = "likes_count", columnDefinition = "INT UNSIGNED DEFAULT 0")
    private Integer likesCount = 0;

    @Column(name = "comments_count", columnDefinition = "INT UNSIGNED DEFAULT 0")
    private Integer commentsCount = 0;
}
