package com.mallchat.register.service;

import com.mallchat.register.dto.EmailRegisterRequest;
import com.mallchat.register.dto.SmsRegisterRequest;

/** 注册服务。 */
public interface RegisterService {
    /** 发送短信验证码（需图形验证码）。 */
    void sendSmsCode(String phone, String captchaKey, String captchaCode);

    /** 发送邮箱验证码（需图形验证码）。 */
    void sendEmailCode(String email, String captchaKey, String captchaCode);

    /** 短信注册。 */
    void registerBySms(SmsRegisterRequest request);

    /** 邮箱注册。 */
    void registerByEmail(EmailRegisterRequest request);
}
