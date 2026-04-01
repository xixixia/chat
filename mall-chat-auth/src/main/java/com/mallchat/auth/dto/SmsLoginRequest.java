package com.mallchat.auth.dto;

import javax.validation.constraints.NotBlank;
import lombok.Data;

/** 短信验证码登录请求。 */
@Data
public class SmsLoginRequest {
    /** 手机号。 */
    @NotBlank
    private String phone;

    /** 短信验证码。 */
    @NotBlank
    private String code;
}
