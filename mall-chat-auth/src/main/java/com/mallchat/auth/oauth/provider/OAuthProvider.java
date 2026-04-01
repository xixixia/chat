package com.mallchat.auth.oauth.provider;

import com.mallchat.auth.vo.LoginResponse;

/**
 * 第三方登录提供者策略接口。
 */
public interface OAuthProvider {
    /**
     * 提供者标识（如 qq、wechat）。
     *
     * @return 标识
     */
    String provider();

    /**
     * 构建授权地址。
     *
     * @param state 随机状态码
     * @return 授权 URL
     */
    String buildAuthorizeUrl(String state);

    /**
     * 使用授权码登录。
     *
     * @param code 授权码
     * @return 登录结果
     */
    LoginResponse loginByCode(String code);
}
