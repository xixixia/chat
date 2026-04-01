package com.mallchat.register.util;

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
}
