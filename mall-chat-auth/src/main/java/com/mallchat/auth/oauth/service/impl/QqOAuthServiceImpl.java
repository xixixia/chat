package com.mallchat.auth.oauth.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mallchat.auth.exception.BusinessException;
import com.mallchat.auth.mapper.UserAccountMapper;
import com.mallchat.auth.model.UserAccount;
import com.mallchat.auth.oauth.dto.QqAccessTokenResponse;
import com.mallchat.auth.oauth.dto.QqOpenIdResponse;
import com.mallchat.auth.oauth.dto.QqUserInfoResponse;
import com.mallchat.auth.oauth.mapper.UserOauthMapper;
import com.mallchat.auth.oauth.model.UserOauth;
import com.mallchat.auth.oauth.provider.OAuthProvider;
import com.mallchat.auth.util.JwtUtil;
import com.mallchat.auth.util.PasswordUtil;
import com.mallchat.auth.vo.LoginResponse;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/** TODO: update docs. */
@Service
public class QqOAuthServiceImpl implements OAuthProvider {
    private static final String PROVIDER = "qq";

    @Value("${app.qq.app-id}")
    private String appId;

    @Value("${app.qq.app-key}")
    private String appKey;

    @Value("${app.qq.redirect-uri}")
    private String redirectUri;

    @Value("${app.qq.scope:get_user_info}")
    private String scope;

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Value("${app.jwt.expire-minutes}")
    private int expireMinutes;

    private final UserOauthMapper userOauthMapper;
    private final UserAccountMapper userAccountMapper;
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public QqOAuthServiceImpl(UserOauthMapper userOauthMapper, UserAccountMapper userAccountMapper) {
        this.userOauthMapper = userOauthMapper;
        this.userAccountMapper = userAccountMapper;
    }

    /** TODO: update docs. */
    @Override
    public String provider() {
        return PROVIDER;
    }

    /** TODO: update docs. */
    @Override
    public String buildAuthorizeUrl(String state) {
        String base = "https://graph.qq.com/oauth2.0/authorize";
        return base
                + "?response_type=code"
                + "&client_id=" + url(appId)
                + "&redirect_uri=" + url(redirectUri)
                + "&scope=" + url(scope)
                + "&state=" + url(state);
    }

    /** TODO: update docs. */
    @Override
    public LoginResponse loginByCode(String code) {
        String accessToken = fetchAccessToken(code);
        QqOpenIdResponse openId = fetchOpenId(accessToken);
        QqUserInfoResponse userInfo = fetchUserInfo(accessToken, openId.getOpenid());

        UserOauth existing = userOauthMapper.findByProviderAndOpenId(PROVIDER, openId.getOpenid());
        Long userId;
        if (existing != null) {
            userId = existing.getUserId();
        } else {
            UserAccount user = new UserAccount();
            user.setUsername(buildUsername(openId.getOpenid()));
            user.setPasswordHash(PasswordUtil.hash(UUID.randomUUID().toString()));
            user.setNickname(userInfo.getNickname() != null ? userInfo.getNickname() : "QQ用户");
            user.setAvatarUrl(userInfo.getFigureurl_qq_1());
            user.setStatus(1);
            userAccountMapper.insert(user);
            userId = user.getId();

            UserOauth oauth = new UserOauth();
            oauth.setUserId(userId);
            oauth.setProvider(PROVIDER);
            oauth.setOpenId(openId.getOpenid());
            oauth.setUnionId(openId.getUnionid());
            userOauthMapper.insert(oauth);
        }

        UserAccount user = userAccountMapper.findById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        LoginResponse response = new LoginResponse();
        response.setUserId(user.getId());
        response.setUsername(user.getUsername());
        response.setNickname(user.getNickname());
        response.setToken(JwtUtil.generateToken(user.getId(), user.getUsername(), jwtSecret, expireMinutes));
        return response;
    }

    private String fetchAccessToken(String code) {
        String url = "https://graph.qq.com/oauth2.0/token";
        String full = url
                + "?grant_type=authorization_code"
                + "&client_id=" + url(appId)
                + "&client_secret=" + url(appKey)
                + "&code=" + url(code)
                + "&redirect_uri=" + url(redirectUri);
        ResponseEntity<String> response = restTemplate.getForEntity(full, String.class);
        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            throw new BusinessException("鑾峰彇 access_token 澶辫触");
        }
        Map<String, String> params = parseQuery(response.getBody());
        String token = params.get("access_token");
        if (token == null) {
            throw new BusinessException("鑾峰彇 access_token 澶辫触");
        }
        return token;
    }

    private QqOpenIdResponse fetchOpenId(String accessToken) {
        String url = "https://graph.qq.com/oauth2.0/me";
        String full = url + "?access_token=" + url(accessToken) + "&fmt=json";
        ResponseEntity<String> response = restTemplate.getForEntity(full, String.class);
        try {
            return objectMapper.readValue(response.getBody(), QqOpenIdResponse.class);
        } catch (Exception e) {
            throw new BusinessException("瑙ｆ瀽 openid 澶辫触");
        }
    }

    private QqUserInfoResponse fetchUserInfo(String accessToken, String openId) {
        String url = "https://graph.qq.com/user/get_user_info";
        String full = url
                + "?access_token=" + url(accessToken)
                + "&oauth_consumer_key=" + url(appId)
                + "&openid=" + url(openId)
                + "&fmt=json";
        ResponseEntity<String> response = restTemplate.getForEntity(full, String.class);
        try {
            return objectMapper.readValue(response.getBody(), QqUserInfoResponse.class);
        } catch (Exception e) {
            throw new BusinessException("鑾峰彇鐢ㄦ埛淇℃伅澶辫触");
        }
    }

    private String buildUsername(String openId) {
        String base = "qq_" + openId;
        if (base.length() > 32) {
            return base.substring(0, 32);
        }
        return base;
    }

    private String url(String value) {
        try {
            return URLEncoder.encode(value, StandardCharsets.UTF_8.name());
        } catch (java.io.UnsupportedEncodingException e) {
            throw new RuntimeException("UTF-8 编码不支持", e);
        }
    }

    private Map<String, String> parseQuery(String query) {
        Map<String, String> map = new HashMap<>();
        String[] parts = query.split("&");
        for (String part : parts) {
            String[] kv = part.split("=", 2);
            if (kv.length == 2) {
                map.put(kv[0], kv[1]);
            }
        }
        return map;
    }
}
