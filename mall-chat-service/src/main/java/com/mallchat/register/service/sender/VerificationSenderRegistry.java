package com.mallchat.register.service.sender;

import com.mallchat.exception.BusinessException;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

/**
 * 验证码发送策略注册表。
 */
@Component
public class VerificationSenderRegistry {
    private final Map<VerifyChannel, VerificationSender> registry = new EnumMap<>(VerifyChannel.class);

    public VerificationSenderRegistry(List<VerificationSender> senders) {
        for (VerificationSender sender : senders) {
            registry.put(sender.channel(), sender);
        }
    }

    /**
     * 获取对应渠道的发送策略。
     *
     * @param channel 渠道
     * @return 发送策略
     */
    public VerificationSender get(VerifyChannel channel) {
        VerificationSender sender = registry.get(channel);
        if (sender == null) {
            throw new BusinessException("验证码渠道未配置");
        }
        return sender;
    }
}
