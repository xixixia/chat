package com.mallchat.model;

import java.time.LocalDateTime;
import lombok.Data;

/**
  * User account entity.
  */
@Data
public class UserAccount {
    /** User ID. */
    private Long id;

    /** Login username. */
    private String username;

    /** Password hash. */
    private String passwordHash;

    /** Display nickname. */
    private String nickname;

    /** Avatar URL. */
    private String avatarUrl;

    /** Status: 1 active, 0 disabled. */
    private Integer status;

    /** Creation time. */
    private LocalDateTime createdAt;

    /** Last update time. */
    private LocalDateTime updatedAt;
}
