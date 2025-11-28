// src/main/java/com/ktbweek4/community/auth/util/CookieUtil.java
package com.ktbweek4.community.auth.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class CookieUtil {

    public static void addHttpOnlyCookie(HttpServletResponse res, String name, String value,
                                         String domain, String path, boolean secure,
                                         String sameSite, int maxAgeSeconds) {
        // SameSite는 표준 Cookie API에 직접 속성이 없어 헤더로 세팅
        String cookie = String.format("%s=%s; Max-Age=%d; Path=%s; HttpOnly; SameSite=%s",
                name, value, maxAgeSeconds, path, sameSite);
        if (domain != null && !domain.isBlank()) cookie += "; Domain=" + domain;
        if (secure) cookie += "; Secure";
        res.addHeader("Set-Cookie", cookie);
    }

    public static void deleteCookie(HttpServletResponse res, String name, String domain, String path, boolean secure, String sameSite) {
        addHttpOnlyCookie(res, name, "", domain, path, secure, sameSite, 0);
    }

    public static String getCookie(HttpServletRequest req, String name) {
        Cookie[] cookies = req.getCookies();
        if (cookies == null) return null;
        for (Cookie c : cookies) if (name.equals(c.getName())) return c.getValue();
        return null;
    }
}