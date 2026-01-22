package com.portfolio.smartgarage.helper.util;

import jakarta.servlet.http.Cookie;

public class CookieUtils {
    public static Cookie createJwtCookie(String token, int expiry) {
        Cookie cookie = new Cookie("jwt", token);
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setPath("/");
        cookie.setMaxAge(expiry);
        return cookie;
    }

    public static Cookie clearCookie(String name) {
        Cookie cookie = new Cookie(name, null);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(0);
        return cookie;
    }
}