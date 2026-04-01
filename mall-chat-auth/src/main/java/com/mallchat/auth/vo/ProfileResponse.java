package com.mallchat.auth.vo;

import lombok.Data;

/** TODO: update docs. */
@Data
public class ProfileResponse {
    private Long userId;
    private String username;
    private String nickname;
    private String avatarUrl;
}
