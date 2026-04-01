package com.mallchat.register.service.captcha;

import com.mallchat.exception.BusinessException;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

/**
 * 图形验证码策略注册表。
 */
@Component
public class CaptchaProviderRegistry {
    private final Map<CaptchaType, CaptchaProvider> registry = new EnumMap<>(CaptchaType.class);

    public CaptchaProviderRegistry(List<CaptchaProvider> providers) {
        for (CaptchaProvider provider : providers) {
            registry.put(provider.type(), provider);
        }
    }

    /**
     * 获取对应类型的验证码提供者。
     *
     * @param type 类型
     * @return 提供者
     */
    public CaptchaProvider get(CaptchaType type) {
        CaptchaProvider provider = registry.get(type);
        if (provider == null) {
            throw new BusinessException("图形验证码未配置");
        }
        return provider;
    }
}
