package com.mallchat.auth.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/** TODO: update docs. */
public final class PasswordUtil {
    private static final BCryptPasswordEncoder ENCODER = new BCryptPasswordEncoder();

    private PasswordUtil() {
    }

    /** TODO: update docs. */
    public static String hash(String raw) {
        return ENCODER.encode(raw);
    }

    /** TODO: update docs. */
    public static boolean matches(String raw, String hash) {
        return ENCODER.matches(raw, hash);
    }
}