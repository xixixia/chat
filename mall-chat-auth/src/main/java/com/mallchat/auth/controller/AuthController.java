package com.mallchat.auth.controller;

import com.mallchat.auth.common.Result;
import com.mallchat.auth.dto.LoginRequest;
import com.mallchat.auth.dto.RegisterRequest;
import com.mallchat.auth.dto.SmsLoginRequest;
import com.mallchat.auth.service.AuthService;
import com.mallchat.auth.vo.LoginResponse;
import javax.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** 认证相关接口。 */
@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /** 注册。 */
    @PostMapping("/register")
    public Result<Boolean> register(@Valid @RequestBody RegisterRequest request) {
        boolean ok = authService.register(request);
        if (!ok) {
            return Result.error("用户名已存在");
        }
        return Result.ok(true);
    }

    /** 账号密码登录。 */
    @PostMapping("/login")
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        if (response == null) {
            return Result.error("用户名或密码错误");
        }
        return Result.ok(response);
    }

    /** 短信验证码登录。 */
    @PostMapping("/login/sms")
    public Result<LoginResponse> loginBySms(@Valid @RequestBody SmsLoginRequest request) {
        LoginResponse response = authService.loginBySms(request);
        return Result.ok(response);
    }
}
