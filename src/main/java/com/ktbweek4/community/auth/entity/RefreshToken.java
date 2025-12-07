// src/main/java/com/ktbweek4/community/auth/entity/RefreshToken.java
package com.ktbweek4.community.auth.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "refresh_token")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefreshToken {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="expiresAt", nullable=false)
    private Instant expiresAt;

    @Column(nullable=false, length=50, unique=true)
    private String jti;

    @Column(nullable=false)
    @Builder.Default
    private boolean revoked = false;

    @Column(nullable=false, unique=true, length=190)
    private String tokenHash;   // 원문 저장 X, SHA-256 등 해시만 저장

    @Column(nullable=false)
    private Long userId;






    // getters/setters/constructors ...
}

