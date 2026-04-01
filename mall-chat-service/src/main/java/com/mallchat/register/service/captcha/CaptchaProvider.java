package com.mallchat.register.service.captcha;

import com.mallchat.register.dto.CaptchaResponse;

/**
 * 图形验证码提供者策略接口。
 */
public interface CaptchaProvider {
    /**
     * 类型。
     *
     * @return 类型
     */
    CaptchaType type();

    /**
     * 生成验证码。
     *
     * @return 响应
     */
    CaptchaResponse generate();

    /**
     * 校验验证码并在成功时删除。
     *
     * @param key  验证码 key
     * @param code 验证码值
     */
    void verify(String key, String code);
}
