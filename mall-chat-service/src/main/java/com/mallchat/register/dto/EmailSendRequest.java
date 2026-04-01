package com.mallchat.register.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import lombok.Data;

/** 邮箱验证码发送请求。 */
@Data
public class EmailSendRequest {
    /** 邮箱地址。 */
    @NotBlank
    @Email
    private String email;

    /** 图形验证码 key。 */
    @NotBlank
    private String captchaKey;

    /** 图形验证码值。 */
    @NotBlank
    private String captchaCode;
}
