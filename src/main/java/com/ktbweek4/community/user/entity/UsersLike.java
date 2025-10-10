package com.ktbweek4.community.user.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter @Setter
//@Table(
//        name = "UsersLike",
//        uniqueConstraints = {
//                @UniqueConstraint(name = "uq_user_target", columnNames = {"user_id", "target_type", "target_id"})
//        },
//        indexes = {
//                @Index(name = "idx_like_target", columnList = "target_type, target_id"),
//                @Index(name = "idx_like_user", columnList = "user_id")
//        }
//)
@Table(name = "users_likes")
public class UsersLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "like_id")
    private Long likeId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // enum ('POST', 'COMMENT')
    @Enumerated(EnumType.STRING)
    @Column(name = "target_type", nullable = false)
    private TargetType targetType;

    @Column(name = "target_id", nullable = false)
    private Long targetId;

    @Column(name = "created_at", insertable = false, columnDefinition = "DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6)", updatable = false)
    private LocalDateTime createdAt;

    protected UsersLike() {}

    public UsersLike(User user, TargetType targetType, Long targetId) {
        this.user = user;
        this.targetType = targetType;
        this.targetId = targetId;
        //this.createdAt = LocalDateTime.now();
    }

    // Enum 정의
    public enum TargetType {
        POST,
        COMMENT
    }

}
