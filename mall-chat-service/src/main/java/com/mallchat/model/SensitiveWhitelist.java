package com.mallchat.model;

import java.time.LocalDateTime;
import lombok.Data;

/**
 * Sensitive whitelist entity.
 */
@Data
public class SensitiveWhitelist {
    /** ID. */
    private Long id;

    /** Whitelist word. */
    private String word;

    /** Enabled status. */
    private Integer enabled;

    /** Creation time. */
    private LocalDateTime createdAt;

    /** Update time. */
    private LocalDateTime updatedAt;
}
