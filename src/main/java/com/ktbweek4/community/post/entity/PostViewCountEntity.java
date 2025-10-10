package com.ktbweek4.community.post.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
@Table(name = "post_view_counts")
public class PostViewCountEntity {

    @Id
    @Column(name = "post_id")
    private Long postId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "post_id")
    private PostEntity post;

    @Column(name = "views_count", columnDefinition = "BIGINT DEFAULT 0", nullable = false)
    private Long viewsCount =  0L;

    protected PostViewCountEntity() {}

    public PostViewCountEntity(PostEntity post) {
        this.post = post;

    }

    // 조회수 증가 편의 메서드
    public void increaseView() {
        this.viewsCount++;
    }
}
