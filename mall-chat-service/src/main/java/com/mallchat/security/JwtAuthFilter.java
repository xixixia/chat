package com.mallchat.security;

import com.mallchat.common.Result;
import com.mallchat.util.JwtUtil;
import io.jsonwebtoken.Claims;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * JWT 鉴权过滤器。
 */
@Component
public class JwtAuthFilter extends OncePerRequestFilter {
    private static final AntPathMatcher MATCHER = new AntPathMatcher();

    @Value("${app.jwt.secret}")
    private String secret;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            boolean requiresAuth = requiresAuth(request);
            String token = resolveToken(request);
            if (token != null && !token.isEmpty()) {
                try {
                    Claims claims = JwtUtil.parse(token, secret);
                    Object userId = claims.get("userId");
                    if (userId != null) {
                        UserContext.setUserId(Long.valueOf(String.valueOf(userId)));
                    }
                } catch (Exception ex) {
                    writeUnauthorized(response, "未登录");
                    return;
                }
            }
            if (requiresAuth && UserContext.getUserId() == null) {
                writeUnauthorized(response, "未登录");
                return;
            }
            filterChain.doFilter(request, response);
        } finally {
            UserContext.clear();
        }
    }

    private boolean requiresAuth(HttpServletRequest request) {
        String path = request.getRequestURI();
        String method = request.getMethod();
        if (MATCHER.match("/health/**", path) || MATCHER.match("/register/**", path)) {
            return false;
        }
        if ("GET".equals(method)) {
            if (MATCHER.match("/posts/me", path) || MATCHER.match("/comments/me", path)) {
                return true;
            }
            return !(MATCHER.match("/posts", path)
                    || MATCHER.match("/posts/*", path)
                    || MATCHER.match("/comments", path)
                    || MATCHER.match("/categories", path));
        }
        if ("POST".equals(method) && (MATCHER.match("/posts", path) || MATCHER.match("/comments", path))) {
            return true;
        }
        if ("DELETE".equals(method) && (MATCHER.match("/posts/*", path) || MATCHER.match("/comments/*", path))) {
            return true;
        }
        return true;
    }

    private String resolveToken(HttpServletRequest request) {
        String auth = request.getHeader("Authorization");
        if (auth != null && auth.startsWith("Bearer ")) {
            return auth.substring(7);
        }
        return null;
    }

    private void writeUnauthorized(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        String body = "{\"code\":" + Result.ERROR + ",\"message\":\"" + message + "\"}";
        response.getWriter().write(body);
    }
}
