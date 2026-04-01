package com.mallchat.auth.model;

import java.time.LocalDateTime;
import lombok.Data;

/** TODO: update docs. */
@Data
public class UserAccount {
    /** TODO: update docs. */
    private Long id;

    /** TODO: update docs. */
    private String username;

    /** TODO: update docs. */
    private String passwordHash;

    /** TODO: update docs. */
    private String nickname;

    /** TODO: update docs. */
    private String avatarUrl;

    /** TODO: update docs. */
    private Integer status;

    /** TODO: update docs. */
    private LocalDateTime createdAt;

    /** TODO: update docs. */
    private LocalDateTime updatedAt;
}