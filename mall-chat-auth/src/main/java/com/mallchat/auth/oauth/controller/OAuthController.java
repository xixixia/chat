package com.mallchat.auth.oauth.controller;

import com.mallchat.auth.common.Result;
import com.mallchat.auth.oauth.provider.OAuthProvider;
import com.mallchat.auth.oauth.provider.OAuthProviderRegistry;
import com.mallchat.auth.vo.LoginResponse;
import java.util.UUID;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 通用第三方登录接口（预留扩展）。
 */
@RestController
@RequestMapping("/oauth")
public class OAuthController {
    private final OAuthProviderRegistry providerRegistry;

    public OAuthController(OAuthProviderRegistry providerRegistry) {
        this.providerRegistry = providerRegistry;
    }

    /**
     * 获取指定提供者的授权地址。
     */
    @GetMapping("/{provider}/authorize")
    public Result<String> authorize(@PathVariable String provider) {
        String state = UUID.randomUUID().toString().replace("-", "");
        OAuthProvider oauth = providerRegistry.get(provider);
        return Result.ok(oauth.buildAuthorizeUrl(state));
    }

    /**
     * 使用授权码登录。
     */
    @GetMapping("/{provider}/login")
    public Result<LoginResponse> login(@PathVariable String provider, @RequestParam String code) {
        OAuthProvider oauth = providerRegistry.get(provider);
        return Result.ok(oauth.loginByCode(code));
    }
}
