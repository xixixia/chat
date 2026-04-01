package com.mallchat.auth.vo;

import lombok.Data;

/** TODO: update docs. */
@Data
public class LoginResponse {
    /** TODO: update docs. */
    private Long userId;

    /** TODO: update docs. */
    private String username;

    /** TODO: update docs. */
    private String nickname;

    /** TODO: update docs. */
    private String token;
}