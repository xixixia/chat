package com.mallchat.register.service.sender;

import com.mallchat.register.service.EmailService;
import org.springframework.stereotype.Component;

/**
 * 邮箱验证码发送策略。
 */
@Component
public class EmailVerificationSender implements VerificationSender {
    private final EmailService emailService;

    public EmailVerificationSender(EmailService emailService) {
        this.emailService = emailService;
    }

    @Override
    public VerifyChannel channel() {
        return VerifyChannel.EMAIL;
    }

    @Override
    public void send(String target, String code) {
        emailService.sendCode(target, code);
    }
}
