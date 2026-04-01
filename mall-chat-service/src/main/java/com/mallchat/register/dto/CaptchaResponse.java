package com.mallchat.register.dto;

import lombok.Data;

/**
 * 图形验证码响应。
 */
@Data
public class CaptchaResponse {
    /** 验证码 key。 */
    private String key;

    /** Base64 图片内容（data:image/svg+xml;base64,...）。 */
    private String image;
}
