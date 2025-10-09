package com.ktbweek4.community.user.entity;

import com.ktbweek4.community.comment.entity.Comment;
import com.ktbweek4.community.post.entity.Post;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;


@Entity
@Getter @Setter
@Table(name = "User") // 실제 디비 ㅔㅌ이블 이름과 매핑
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // auto_increment
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "email", length = 30, nullable = false, unique = true)
    private String email;

    @Column(name = "nickname", length = 20, nullable = false, unique = true)
    private String nickname;

    @Column(name = "password", length = 255, nullable = false)
    private String password;

    // 연관 관계
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "image_id", insertable = false, updatable = false)
    private Image image_id;

    @OneToMany(mappedBy = "author")
    private List<Post> posts = new ArrayList<>();

    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<UsersLike> likes = new ArrayList<>();

    // tinyint(1) -> boolean 으로 매핑 가능
    @Column(name = "is_deleted")
    private Boolean is_deleted = false;


    // JPA의 기본 생성자
    protected User() {}

    // 생성자 오버로딩
    public User(String email, String nickname, String password, Image imageId) {
        this.email = email;
        this.nickname = nickname;
        this.password = password;
        this.image_id = imageId;
        this.is_deleted = false;
    }
}
