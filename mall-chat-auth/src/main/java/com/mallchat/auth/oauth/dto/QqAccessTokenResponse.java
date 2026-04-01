package com.mallchat.auth.oauth.dto;

import lombok.Data;

/** TODO: update docs. */
@Data
public class QqAccessTokenResponse {
    /** access_token */
    private String accessToken;

    /** expires_in */
    private Long expiresIn;
}