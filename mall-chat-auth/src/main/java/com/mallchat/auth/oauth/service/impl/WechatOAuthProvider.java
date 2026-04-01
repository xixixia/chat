package com.mallchat.auth.oauth.service.impl;

import com.mallchat.auth.exception.BusinessException;
import com.mallchat.auth.oauth.provider.OAuthProvider;
import com.mallchat.auth.vo.LoginResponse;
import org.springframework.stereotype.Service;

/**
 * 微信登录占位实现。
 */
@Service
public class WechatOAuthProvider implements OAuthProvider {
    @Override
    public String provider() {
        return "wechat";
    }

    @Override
    public String buildAuthorizeUrl(String state) {
        throw new BusinessException("微信登录暂未实现");
    }

    @Override
    public LoginResponse loginByCode(String code) {
        throw new BusinessException("微信登录暂未实现");
    }
}
