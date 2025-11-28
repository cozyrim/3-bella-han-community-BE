// src/main/java/com/ktbweek4/community/auth/repository/RefreshTokenRepository.java
package com.ktbweek4.community.auth.repository;

import com.ktbweek4.community.auth.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByTokenHash(String tokenHash);
    Optional<RefreshToken> findByJti(String jti);
}