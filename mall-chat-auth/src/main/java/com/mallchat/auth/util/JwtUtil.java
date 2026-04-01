package com.mallchat.auth.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

/** TODO: update docs. */
public final class JwtUtil {
    private JwtUtil() {
    }

    /** TODO: update docs. */
    public static String generateToken(Long userId, String username, String secret, int expireMinutes) {
        Key key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        long now = System.currentTimeMillis();
        return Jwts.builder()
                .setSubject(username)
                .claim("userId", userId)
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + expireMinutes * 60L * 1000L))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /** TODO: update docs. */
    public static Claims parse(String token, String secret) {
        Key key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
    }
}
