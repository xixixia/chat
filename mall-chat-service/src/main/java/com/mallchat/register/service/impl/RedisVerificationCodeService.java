package com.mallchat.register.service.impl;

import com.mallchat.register.service.VerificationCodeService;
import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

/** TODO: update docs. */
@Service
public class RedisVerificationCodeService implements VerificationCodeService {
    private static final String SMS_PREFIX = "verify:sms:";
    private static final String EMAIL_PREFIX = "verify:email:";

    private final StringRedisTemplate redisTemplate;

    @Value("${app.verify.expire-minutes:5}")
    private int expireMinutes;

    public RedisVerificationCodeService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /** TODO: update docs. */
    @Override
    public void saveSmsCode(String phone, String code) {
        redisTemplate.opsForValue().set(SMS_PREFIX + phone, code, Duration.ofMinutes(expireMinutes));
    }

    /** TODO: update docs. */
    @Override
    public boolean verifySmsCode(String phone, String code) {
        String value = redisTemplate.opsForValue().get(SMS_PREFIX + phone);
        return value != null && value.equals(code);
    }

    /** TODO: update docs. */
    @Override
    public void saveEmailCode(String email, String code) {
        redisTemplate.opsForValue().set(EMAIL_PREFIX + email, code, Duration.ofMinutes(expireMinutes));
    }

    /** TODO: update docs. */
    @Override
    public boolean verifyEmailCode(String email, String code) {
        String value = redisTemplate.opsForValue().get(EMAIL_PREFIX + email);
        return value != null && value.equals(code);
    }
}
