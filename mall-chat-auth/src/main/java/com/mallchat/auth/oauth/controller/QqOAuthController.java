package com.mallchat.auth.oauth.controller;

import com.mallchat.auth.common.Result;
import com.mallchat.auth.oauth.dto.QqAuthorizeResponse;
import com.mallchat.auth.oauth.provider.OAuthProvider;
import com.mallchat.auth.oauth.provider.OAuthProviderRegistry;
import com.mallchat.auth.vo.LoginResponse;
import java.util.UUID;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** TODO: update docs. */
@RestController
@RequestMapping("/oauth/qq")
public class QqOAuthController {
    private final OAuthProviderRegistry providerRegistry;

    public QqOAuthController(OAuthProviderRegistry providerRegistry) {
        this.providerRegistry = providerRegistry;
    }

    /** TODO: update docs. */
    @GetMapping("/authorize")
    public Result<QqAuthorizeResponse> authorize() {
        String state = UUID.randomUUID().toString().replace("-", "");
        OAuthProvider provider = providerRegistry.get("qq");
        String url = provider.buildAuthorizeUrl(state);
        QqAuthorizeResponse resp = new QqAuthorizeResponse();
        resp.setUrl(url);
        resp.setState(state);
        return Result.ok(resp);
    }

    /** TODO: update docs. */
    @GetMapping("/login")
    public Result<LoginResponse> login(@RequestParam String code) {
        OAuthProvider provider = providerRegistry.get("qq");
        return Result.ok(provider.loginByCode(code));
    }
}
