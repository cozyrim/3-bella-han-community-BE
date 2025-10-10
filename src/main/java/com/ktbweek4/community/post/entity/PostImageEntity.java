package com.ktbweek4.community.post.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "postimages")
@Getter @Setter
public class PostImageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long postImageId;

    @Column(name = "post_image_url", length = 255, nullable = false)
    private String postImageUrl;

    @Column(name = "create_at", insertable = false, updatable = false)
    private LocalDateTime createAt;

    @Column(name = "order_index")
    private Byte orderIndex;

    @Column(name = "is_primary", nullable = false)
    private Boolean isPrimary = false;

    // post n : 1 조인테이블의 fk
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "post_id")
    private PostEntity post;

}
