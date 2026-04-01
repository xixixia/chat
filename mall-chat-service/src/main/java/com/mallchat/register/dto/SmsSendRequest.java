package com.mallchat.register.dto;

import javax.validation.constraints.NotBlank;
import lombok.Data;

/** 短信验证码发送请求。 */
@Data
public class SmsSendRequest {
    /** 手机号。 */
    @NotBlank
    private String phone;

    /** 图形验证码 key。 */
    @NotBlank
    private String captchaKey;

    /** 图形验证码值。 */
    @NotBlank
    private String captchaCode;
}
