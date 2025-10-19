package com.ktbweek4.community.user.entity;

import com.ktbweek4.community.comment.entity.Comment;
import com.ktbweek4.community.common.BaseTimeEntity;
import com.ktbweek4.community.post.entity.PostEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;


@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "users") // 실제 DB 테이블 이름과 매핑
public class User extends BaseTimeEntity {
    
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

    @Column(name = "user_profile_url", length = 255)
    private String userProfileUrl;

    // tinyint(1) -> boolean 으로 매핑 가능
    @Column(name = "is_deleted")
    private Boolean isDeleted = false;

    @OneToMany(mappedBy = "author")
    private List<PostEntity> posts = new ArrayList<>();

    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<UsersLike> likes = new ArrayList<>();
}
