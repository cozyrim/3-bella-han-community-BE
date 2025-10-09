package com.ktbweek4.community.user.entity;

import com.ktbweek4.community.post.entity.Post;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "PostImage")
@Getter @Setter
public class PostImage {

    @EmbeddedId
    private PostImageId id = new PostImageId();

    // post n : 1 조인테이블의 fk
    @MapsId("postId") // 복합키(post_id, image_id) 중에 post_id 매핑
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "post_id")
    private Post post;

    // image N ; 1 관계
    @MapsId("imageId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "image_id")
    private Image image;

    @Column(name = "order_index")
    private Integer orderIndex;

    @Column(name = "is_primary", nullable = false)
    private Boolean isPrimary = false;

    protected PostImage() {}

    public PostImage(Post post, Image image, Integer orderIndex, Boolean isPrimary) {
        this.post = post;
        this.image = image;
        this.orderIndex = orderIndex;
        this.isPrimary = (isPrimary != null) ? isPrimary : false;
        syncIdFromAssociations();
    }
    @PrePersist
    private void prePersist() {
        if (isPrimary == null) isPrimary = false;
        syncIdFromAssociations();
    }

    public void syncIdFromAssociations() {
        if (post != null && image != null ) {
            this.id = new PostImageId();
        }
    }
}
