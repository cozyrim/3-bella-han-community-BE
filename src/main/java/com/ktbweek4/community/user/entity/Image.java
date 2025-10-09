package com.ktbweek4.community.user.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
@Table(name = "Image")
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "image_id")
    private Long imageId;

    @Column(name = "url", length = 255, nullable = false)
    private String url;

    @Column(name = "create_at", insertable = false, updatable = false)
    private LocalDateTime createAt;

    // image 1 : n postImage
    @OneToMany(mappedBy = "image", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostImage> postImages = new ArrayList<>();

    // User 프로필 이미지로 쓰이는 경우
    @OneToMany(mappedBy = "profileImage")
    private List<User> users = new ArrayList<>();


    protected Image() {}

    public Image(String url) {
        this.url = url;
        this.createAt = LocalDateTime.now();
    }
}
