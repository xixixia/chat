package com.mallchat.auth.oauth.provider;

import com.mallchat.auth.exception.BusinessException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

/**
 * 第三方登录提供者注册表。
 */
@Component
public class OAuthProviderRegistry {
    private final Map<String, OAuthProvider> registry = new HashMap<>();

    public OAuthProviderRegistry(List<OAuthProvider> providers) {
        for (OAuthProvider provider : providers) {
            registry.put(provider.provider(), provider);
        }
    }

    /**
     * 获取提供者。
     *
     * @param provider 提供者标识
     * @return 提供者
     */
    public OAuthProvider get(String provider) {
        OAuthProvider found = registry.get(provider);
        if (found == null) {
            throw new BusinessException("不支持的第三方登录");
        }
        return found;
    }
}
