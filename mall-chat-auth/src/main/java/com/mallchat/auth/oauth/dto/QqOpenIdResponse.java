package com.mallchat.auth.oauth.dto;

import lombok.Data;

/** TODO: update docs. */
@Data
public class QqOpenIdResponse {
    /** openid */
    private String openid;

    /** client_id */
    private String clientId;

    /** unionid */
    private String unionid;
}