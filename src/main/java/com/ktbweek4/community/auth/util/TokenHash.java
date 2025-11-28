// src/main/java/com/ktbweek4/community/auth/util/TokenHash.java
package com.ktbweek4.community.auth.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class TokenHash {
    public static String sha256(String token) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] d = md.digest(token.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(d.length * 2);
            for (byte b : d) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}