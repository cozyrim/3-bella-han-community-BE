package com.ktbweek4.community.post.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
@Table(name = "Post_View")
public class PostViewCount {

    @Id
    @Column(name = "post_id")
    private Long postId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "post_id")
    private Post post;

    @Column(name = "views count", columnDefinition = "BIGINT DEFAULT 0", nullable = false)
    private Long viewsCount =  0L;

    protected PostViewCount() {}

    public PostViewCount(Long postId) {
        this.postId = postId;
        this.viewsCount = 0L;
    }

    // 조회수 증가 편의 메서드
    public void increaseView() {
        this.viewsCount++;
    }
}
