package com.mallchat.auth.oauth.dto;

import lombok.Data;

/** TODO: update docs. */
@Data
public class QqAuthorizeResponse {
    /** 授权地址 */
    private String url;

    /** state */
    private String state;
}
