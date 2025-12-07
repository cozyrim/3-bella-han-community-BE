package com.ktbweek4.community.post.entity;

import com.ktbweek4.community.comment.entity.Comment;
import com.ktbweek4.community.common.BaseTimeEntity;
import com.ktbweek4.community.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @ Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "posts")
public class PostEntity extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // auto_increment
    @Column(name = "post_id")
    private Long postId;


    @Column(name = "title", length = 45)
    private String title;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Builder.Default
    @Column(name = "likes_count", columnDefinition = "INT UNSIGNED DEFAULT 0")
    private Integer likesCount = 0;

    @Builder.Default
    @Column(name = "comments_count", columnDefinition = "INT UNSIGNED DEFAULT 0")
    private Integer commentsCount = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User author;

    // post 1 : N comment (글 삭제 시 댓글도 함께 제거)
    @Builder.Default
    @OneToMany(mappedBy = "post", cascade = CascadeType.REMOVE, orphanRemoval = true)
    @OrderBy("createdAt ASC") // 댓글 리스트 항상 오래된 순으로 정렬해서 로딩
    private List<Comment> comments = new ArrayList<>();

    // post 1 : N postImage (연결/순서 관리 같이 함)
    @Builder.Default
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostImageEntity> postImages = new ArrayList<>();

    // post 1 : 1 postviewCount
//    @OneToOne(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
//    private PostViewCountEntity viewCount;

    @Builder.Default
    @Column(name = "view_count", columnDefinition = "BIGINT UNSIGNED DEFAULT 0", nullable = false)
    private Long viewCount = 0L;

    public void addImage(PostImageEntity image) {
        this.getPostImages().add(image);
        image.setPost(this);
    }


    public void removeImage(PostImageEntity image) {
        this.getPostImages().remove(image);
        image.setPost(null);
    }
}
