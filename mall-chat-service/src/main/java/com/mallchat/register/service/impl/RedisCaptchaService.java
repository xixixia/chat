package com.mallchat.register.service.impl;

import com.mallchat.exception.BusinessException;
import com.mallchat.register.dto.CaptchaResponse;
import com.mallchat.register.service.captcha.CaptchaProvider;
import com.mallchat.register.service.captcha.CaptchaType;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Base64;
import java.util.Random;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

/**
 * 基于 Redis 的图形验证码服务。
 */
@Service
public class RedisCaptchaService implements CaptchaProvider {
    private static final String CAPTCHA_PREFIX = "captcha:";
    private static final String ALPHABET = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private static final Random RANDOM = new Random();

    private final StringRedisTemplate redisTemplate;

    @Value("${app.captcha.expire-minutes:5}")
    private int expireMinutes;

    public RedisCaptchaService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public CaptchaType type() {
        return CaptchaType.IMAGE;
    }

    @Override
    public CaptchaResponse generate() {
        String code = randomCode(5);
        String key = UUID.randomUUID().toString().replace("-", "");
        redisTemplate.opsForValue().set(CAPTCHA_PREFIX + key, code, Duration.ofMinutes(expireMinutes));
        String svg = buildSvg(code);
        String base64 = Base64.getEncoder().encodeToString(svg.getBytes(StandardCharsets.UTF_8));
        CaptchaResponse response = new CaptchaResponse();
        response.setKey(key);
        response.setImage("data:image/svg+xml;base64," + base64);
        return response;
    }

    @Override
    public void verify(String key, String code) {
        if (key == null || key.trim().isEmpty() || code == null || code.trim().isEmpty()) {
            throw new BusinessException("图形验证码不能为空");
        }
        String cacheKey = CAPTCHA_PREFIX + key.trim();
        String cached = redisTemplate.opsForValue().get(cacheKey);
        if (cached == null || !cached.equalsIgnoreCase(code.trim())) {
            throw new BusinessException("图形验证码错误或已过期");
        }
        redisTemplate.delete(cacheKey);
    }

    private String randomCode(int len) {
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            int idx = RANDOM.nextInt(ALPHABET.length());
            sb.append(ALPHABET.charAt(idx));
        }
        return sb.toString();
    }

    private String buildSvg(String code) {
        int width = 120;
        int height = 40;
        int fontSize = 22;
        StringBuilder sb = new StringBuilder();
        sb.append("<svg xmlns='http://www.w3.org/2000/svg' width='").append(width)
                .append("' height='").append(height).append("' viewBox='0 0 ")
                .append(width).append(" ").append(height).append("'>");
        sb.append("<rect width='100%' height='100%' fill='#f8fafc'/>");
        for (int i = 0; i < 4; i++) {
            int x1 = RANDOM.nextInt(width);
            int y1 = RANDOM.nextInt(height);
            int x2 = RANDOM.nextInt(width);
            int y2 = RANDOM.nextInt(height);
            sb.append("<line x1='").append(x1).append("' y1='").append(y1)
                    .append("' x2='").append(x2).append("' y2='").append(y2)
                    .append("' stroke='#cbd5f5' stroke-width='1'/>");
        }
        int gap = width / (code.length() + 1);
        for (int i = 0; i < code.length(); i++) {
            int x = gap * (i + 1) - 6;
            int y = 26 + RANDOM.nextInt(6);
            int rotate = RANDOM.nextInt(21) - 10;
            sb.append("<text x='").append(x).append("' y='").append(y)
                    .append("' font-size='").append(fontSize)
                    .append("' fill='#1e293b' font-family='Arial' ")
                    .append("transform='rotate(").append(rotate).append(" ")
                    .append(x).append(" ").append(y).append(")'>")
                    .append(code.charAt(i)).append("</text>");
        }
        sb.append("</svg>");
        return sb.toString();
    }
}
