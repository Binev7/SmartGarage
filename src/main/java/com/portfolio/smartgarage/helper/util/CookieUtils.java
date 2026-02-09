package com.portfolio.smartgarage.helper.util;

import jakarta.servlet.http.Cookie;

public class CookieUtils {

    public static Cookie createJwtCookie(String token, int duration) {
        Cookie cookie = new Cookie("jwt", token);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(duration);

        return cookie;
    }

    public static Cookie clearCookie(String name) {
        Cookie cookie = new Cookie(name, null);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        return cookie;
    }
}