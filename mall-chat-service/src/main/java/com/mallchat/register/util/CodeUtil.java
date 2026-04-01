package com.mallchat.register.util;

import java.security.SecureRandom;

/** TODO: update docs. */
public final class CodeUtil {
    private static final SecureRandom RANDOM = new SecureRandom();

    private CodeUtil() {
    }

    /** TODO: update docs. */
    public static String generate6() {
        int code = 100000 + RANDOM.nextInt(900000);
        return String.valueOf(code);
    }
}
