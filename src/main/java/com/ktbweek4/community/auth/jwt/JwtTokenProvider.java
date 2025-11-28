package com.ktbweek4.community.auth.jwt;

import com.ktbweek4.community.user.dto.CustomUserDetails;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;


@Component
public class JwtTokenProvider {

    private final Key key;
    private final long accessValidityMs;
    private final long refreshValidityMs;


    public JwtTokenProvider(
            @Value("${jwt.secret}") String secretKeyBase64,
            @Value("${jwt.access-expiration}") long accessValidityMs,
            @Value("${jwt.refresh-expiration}") long refreshValidityMs
    ) {
        byte[] secret = Decoders.BASE64.decode(secretKeyBase64);
        if (secret.length < 32) throw new IllegalArgumentException("jwt.secret must be >= 256-bit base64");
        this.key = Keys.hmacShaKeyFor(secret);
        this.accessValidityMs = accessValidityMs;
        this.refreshValidityMs = refreshValidityMs;
    }

    public String generateAccessToken(CustomUserDetails user) {
        return buildToken(user, accessValidityMs, null);
    }

    public String generateRefreshToken(CustomUserDetails user, String jti) {
        return buildToken(user, refreshValidityMs, jti);
    }



    private String buildToken(CustomUserDetails user, long validityMs, String jtiOrNull) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + validityMs);

        JwtBuilder b = Jwts.builder()
                .setSubject(user.getUsername()) // email
                .claim("userId", user.getUser().getUserId())
                .claim("nickname", user.getUser().getNickname())
                .setIssuedAt(now)
                .setExpiration(exp)
                .signWith(key, SignatureAlgorithm.HS256);

        if (jtiOrNull != null) b.setId(jtiOrNull);
        return b.compact();
    }

    public Jws<Claims> parseJws(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .setAllowedClockSkewSeconds(60)
                .build()
                .parseClaimsJws(token);
    }

    public String getUsername(String token) { return parseJws(token).getBody().getSubject(); }

    public String getJti(String token) { return parseJws(token).getBody().getId(); }

    public boolean isExpired(String token) {
        Date exp = parseJws(token).getBody().getExpiration();
        return exp.before(new Date());
    }

    public boolean validateToken(String token) {
        try {
            parseJws(token); // 서명, 만료, 변조 여부 검증
            return true;
        } catch (ExpiredJwtException e) {
            System.out.println("JWT expired: " + e.getMessage());
        } catch (JwtException | IllegalArgumentException e) {
            System.out.println("Invalid JWT: " + e.getMessage());
        }
        return false;
    }


    public String newJti() { return UUID.randomUUID().toString(); }

    public Instant getExpiryInstant(String token) {
        return parseJws(token).getBody().getExpiration().toInstant();
    }
}

