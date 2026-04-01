package com.mallchat.register.service.impl;

import com.mallchat.exception.BusinessException;
import com.mallchat.register.service.EmailService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/** TODO: update docs. */
@Service
public class SmtpEmailService implements EmailService {
    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String from;

    @Value("${app.email.subject:验证码}")
    private String subject;

    public SmtpEmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /** TODO: update docs. */
    @Override
    public void sendCode(String email, String code) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(from);
            message.setTo(email);
            message.setSubject(subject);
            message.setText("您的验证码是：" + code + "，有效期内请勿泄露。");
            mailSender.send(message);
        } catch (Exception e) {
            throw new BusinessException("邮件发送失败: " + e.getMessage());
        }
    }
}
