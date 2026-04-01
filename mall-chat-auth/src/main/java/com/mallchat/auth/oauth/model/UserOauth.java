package com.mallchat.auth.oauth.model;

import java.time.LocalDateTime;
import lombok.Data;

/** TODO: update docs. */
@Data
public class UserOauth {
    /** TODO: update docs. */
    private Long id;

    /** TODO: update docs. */
    private Long userId;

    /** TODO: update docs. */
    private String provider;

    /** TODO: update docs. */
    private String openId;

    /** TODO: update docs. */
    private String unionId;

    /** TODO: update docs. */
    private LocalDateTime createdAt;

    /** TODO: update docs. */
    private LocalDateTime updatedAt;
}