package com.mallchat.auth.oauth.dto;

import lombok.Data;

/** TODO: update docs. */
@Data
public class QqUserInfoResponse {
    /** 昵称 */
    private String nickname;

    /** 头像 URL */
    private String figureurl_qq_1;
}