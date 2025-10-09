package com.ktbweek4.community.user.entity;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Column;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class PostImageId implements Serializable{

    @Column(name = "post_id")
    private Long postId;

    @Column(name = "image_id")
    private Long imageId;

    protected PostImageId() {}

    public PostImageId(Long postId, Long imageId) {
        this.postId = postId;
        this.imageId = imageId;
    }

    public Long getPostId() {
        return postId;
    }

    public Long getImageId() {
        return imageId;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PostImageId)) return false;
        PostImageId that = (PostImageId) o;
        return Objects.equals(postId, that.postId) &&
                Objects.equals(imageId, that.imageId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(postId, imageId);
    }
}
