package com.mallchat.register.service.impl;

import com.mallchat.exception.BusinessException;
import com.mallchat.mapper.UserAccountMapper;
import com.mallchat.model.UserAccount;
import com.mallchat.register.dto.EmailRegisterRequest;
import com.mallchat.register.dto.SmsRegisterRequest;
import com.mallchat.register.service.captcha.CaptchaProviderRegistry;
import com.mallchat.register.service.captcha.CaptchaType;
import com.mallchat.register.service.sender.VerificationSenderRegistry;
import com.mallchat.register.service.sender.VerifyChannel;
import com.mallchat.register.service.RegisterService;
import com.mallchat.register.service.VerificationCodeService;
import com.mallchat.register.util.CodeUtil;
import com.mallchat.register.util.PasswordUtil;
import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

/** 注册与验证码服务实现。 */
@Service
public class RegisterServiceImpl implements RegisterService {
    private static final String SMS_SEND_KEY = "verify:send:sms:";
    private static final String EMAIL_SEND_KEY = "verify:send:email:";
    private static final String SMS_FAIL_KEY = "verify:fail:sms:";
    private static final String EMAIL_FAIL_KEY = "verify:fail:email:";

    private final VerificationCodeService verificationCodeService;
    private final CaptchaProviderRegistry captchaProviderRegistry;
    private final VerificationSenderRegistry senderRegistry;
    private final UserAccountMapper userAccountMapper;
    private final StringRedisTemplate redisTemplate;

    @Value("${app.verify.expire-minutes:5}")
    private int expireMinutes;

    @Value("${app.verify.send-interval-seconds:60}")
    private int sendIntervalSeconds;

    @Value("${app.verify.max-attempts:5}")
    private int maxAttempts;

    public RegisterServiceImpl(VerificationCodeService verificationCodeService,
                               CaptchaProviderRegistry captchaProviderRegistry,
                               VerificationSenderRegistry senderRegistry,
                               UserAccountMapper userAccountMapper,
                               StringRedisTemplate redisTemplate) {
        this.verificationCodeService = verificationCodeService;
        this.captchaProviderRegistry = captchaProviderRegistry;
        this.senderRegistry = senderRegistry;
        this.userAccountMapper = userAccountMapper;
        this.redisTemplate = redisTemplate;
    }

    /** 发送短信验证码（需图形验证码）。 */
    @Override
    public void sendSmsCode(String phone, String captchaKey, String captchaCode) {
        checkSendInterval(SMS_SEND_KEY + phone);
        captchaProviderRegistry.get(CaptchaType.IMAGE).verify(captchaKey, captchaCode);
        String code = CodeUtil.generate6();
        senderRegistry.get(VerifyChannel.SMS).send(phone, code);
        verificationCodeService.saveSmsCode(phone, code);
        markSendInterval(SMS_SEND_KEY + phone);
    }

    /** 发送邮箱验证码（需图形验证码）。 */
    @Override
    public void sendEmailCode(String email, String captchaKey, String captchaCode) {
        checkSendInterval(EMAIL_SEND_KEY + email);
        captchaProviderRegistry.get(CaptchaType.IMAGE).verify(captchaKey, captchaCode);
        String code = CodeUtil.generate6();
        senderRegistry.get(VerifyChannel.EMAIL).send(email, code);
        verificationCodeService.saveEmailCode(email, code);
        markSendInterval(EMAIL_SEND_KEY + email);
    }

    /** 短信注册。 */
    @Override
    public void registerBySms(SmsRegisterRequest request) {
        checkAttempts(SMS_FAIL_KEY + request.getPhone());
        boolean ok = verificationCodeService.verifySmsCode(request.getPhone(), request.getCode());
        if (!ok) {
            recordFail(SMS_FAIL_KEY + request.getPhone());
            throw new BusinessException("验证码错误或已过期");
        }
        clearFail(SMS_FAIL_KEY + request.getPhone());
        if (userAccountMapper.findByUsername(request.getPhone()) != null) {
            throw new BusinessException("该手机号已注册");
        }
        UserAccount user = new UserAccount();
        user.setUsername(request.getPhone());
        user.setPasswordHash(PasswordUtil.hash(request.getPassword()));
        user.setNickname(request.getNickname());
        user.setStatus(1);
        userAccountMapper.insert(user);
    }

    /** 邮箱注册。 */
    @Override
    public void registerByEmail(EmailRegisterRequest request) {
        checkAttempts(EMAIL_FAIL_KEY + request.getEmail());
        boolean ok = verificationCodeService.verifyEmailCode(request.getEmail(), request.getCode());
        if (!ok) {
            recordFail(EMAIL_FAIL_KEY + request.getEmail());
            throw new BusinessException("验证码错误或已过期");
        }
        clearFail(EMAIL_FAIL_KEY + request.getEmail());
        if (userAccountMapper.findByUsername(request.getEmail()) != null) {
            throw new BusinessException("该邮箱已注册");
        }
        UserAccount user = new UserAccount();
        user.setUsername(request.getEmail());
        user.setPasswordHash(PasswordUtil.hash(request.getPassword()));
        user.setNickname(request.getNickname());
        user.setStatus(1);
        userAccountMapper.insert(user);
    }

    private void checkSendInterval(String key) {
        String value = redisTemplate.opsForValue().get(key);
        if (value != null) {
            throw new BusinessException("发送过于频繁，请稍后再试");
        }
    }

    private void markSendInterval(String key) {
        redisTemplate.opsForValue().set(key, "1", Duration.ofSeconds(sendIntervalSeconds));
    }

    private void checkAttempts(String key) {
        String value = redisTemplate.opsForValue().get(key);
        if (value != null && Integer.parseInt(value) >= maxAttempts) {
            throw new BusinessException("验证码错误次数过多，请稍后再试");
        }
    }

    private void recordFail(String key) {
        Long count = redisTemplate.opsForValue().increment(key);
        if (count != null && count == 1L) {
            redisTemplate.expire(key, Duration.ofMinutes(expireMinutes));
        }
    }

    private void clearFail(String key) {
        redisTemplate.delete(key);
    }
}
