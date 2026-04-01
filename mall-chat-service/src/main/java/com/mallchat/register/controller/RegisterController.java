package com.mallchat.register.controller;

import com.mallchat.common.Result;
import com.mallchat.register.dto.CaptchaResponse;
import com.mallchat.register.dto.EmailRegisterRequest;
import com.mallchat.register.dto.EmailSendRequest;
import com.mallchat.register.dto.SmsRegisterRequest;
import com.mallchat.register.dto.SmsSendRequest;
import com.mallchat.register.service.captcha.CaptchaProviderRegistry;
import com.mallchat.register.service.captcha.CaptchaType;
import com.mallchat.register.service.RegisterService;
import javax.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** 注册与验证码相关接口。 */
@RestController
@RequestMapping("/register")
public class RegisterController {
    private final RegisterService registerService;
    private final CaptchaProviderRegistry captchaProviderRegistry;

    public RegisterController(RegisterService registerService, CaptchaProviderRegistry captchaProviderRegistry) {
        this.registerService = registerService;
        this.captchaProviderRegistry = captchaProviderRegistry;
    }

    /** 获取图形验证码。 */
    @PostMapping("/captcha")
    public Result<CaptchaResponse> captcha() {
        return Result.ok(captchaProviderRegistry.get(CaptchaType.IMAGE).generate());
    }

    /** 发送短信验证码（需图形验证码）。 */
    @PostMapping("/sms/send")
    public Result<Boolean> sendSms(@Valid @RequestBody SmsSendRequest request) {
        registerService.sendSmsCode(request.getPhone(), request.getCaptchaKey(), request.getCaptchaCode());
        return Result.ok(true);
    }

    /** 短信注册。 */
    @PostMapping("/sms")
    public Result<Boolean> registerSms(@Valid @RequestBody SmsRegisterRequest request) {
        registerService.registerBySms(request);
        return Result.ok(true);
    }

    /** 发送邮箱验证码（需图形验证码）。 */
    @PostMapping("/email/send")
    public Result<Boolean> sendEmail(@Valid @RequestBody EmailSendRequest request) {
        registerService.sendEmailCode(request.getEmail(), request.getCaptchaKey(), request.getCaptchaCode());
        return Result.ok(true);
    }

    /** 邮箱注册。 */
    @PostMapping("/email")
    public Result<Boolean> registerEmail(@Valid @RequestBody EmailRegisterRequest request) {
        registerService.registerByEmail(request);
        return Result.ok(true);
    }
}
