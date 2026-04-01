package com.mallchat.register.service.sender;

import com.mallchat.register.service.SmsService;
import org.springframework.stereotype.Component;

/**
 * 短信验证码发送策略。
 */
@Component
public class SmsVerificationSender implements VerificationSender {
    private final SmsService smsService;

    public SmsVerificationSender(SmsService smsService) {
        this.smsService = smsService;
    }

    @Override
    public VerifyChannel channel() {
        return VerifyChannel.SMS;
    }

    @Override
    public void send(String target, String code) {
        smsService.sendCode(target, code);
    }
}
