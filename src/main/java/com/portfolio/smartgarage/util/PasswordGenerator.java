package com.portfolio.smartgarage.util;

import java.security.SecureRandom;


public class PasswordGenerator {
    private static final String CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+-*^!@#$";
    private static final SecureRandom random = new SecureRandom();

    public static String generate() {
        StringBuilder sb = new StringBuilder();
        sb.append("ABC".charAt(random.nextInt(3)));
        sb.append("123".charAt(random.nextInt(3)));
        sb.append("+-*".charAt(random.nextInt(3)));

        for (int i = 0; i < 7; i++) {
            sb.append(CHARS.charAt(random.nextInt(CHARS.length())));
        }
        return sb.toString();
    }
}
