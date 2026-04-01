package com.mallchat.auth.service;

import com.mallchat.auth.dto.LoginRequest;
import com.mallchat.auth.dto.RegisterRequest;
import com.mallchat.auth.dto.SmsLoginRequest;
import com.mallchat.auth.dto.UpdateProfileRequest;
import com.mallchat.auth.vo.LoginResponse;
import com.mallchat.auth.vo.ProfileResponse;

/** 认证服务。 */
public interface AuthService {
    /** 注册。 */
    boolean register(RegisterRequest request);

    /** 账号密码登录。 */
    LoginResponse login(LoginRequest request);

    /** 短信验证码登录。 */
    LoginResponse loginBySms(SmsLoginRequest request);

    /** 获取个人资料。 */
    ProfileResponse getProfile(Long userId);

    /** 更新个人资料。 */
    ProfileResponse updateProfile(Long userId, UpdateProfileRequest request);
}
