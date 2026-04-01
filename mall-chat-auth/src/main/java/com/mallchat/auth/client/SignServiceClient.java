package com.mallchat.auth.client;

import com.mallchat.auth.common.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;

/**
 * 签到服务 Feign 客户端。
 */
@FeignClient(name = "mall-chat-service")
public interface SignServiceClient {
    /**
     * 触发业务服务的签到。
     *
     * @param authorization Bearer Token
     * @return 结果
     */
    @PostMapping("/sign/check-in")
    Result<Object> checkIn(@RequestHeader("Authorization") String authorization);
}
