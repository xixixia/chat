package com.mallchat.register.service.sender;

/**
 * 验证码发送策略接口。
 */
public interface VerificationSender {
    /**
     * 支持的渠道类型。
     *
     * @return 渠道
     */
    VerifyChannel channel();

    /**
     * 发送验证码。
     *
     * @param target 目标（手机号或邮箱）
     * @param code   验证码
     */
    void send(String target, String code);
}
