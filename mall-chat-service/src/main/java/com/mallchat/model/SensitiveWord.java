package com.mallchat.model;

import java.time.LocalDateTime;
import lombok.Data;

/**
 * Sensitive word entity.
 */
@Data
public class SensitiveWord {
    /** ID. */
    private Long id;

    /** Sensitive word. */
    private String word;

    /** Level: 1 low, 2 medium, 3 high. */
    private Integer level;

    /** Enabled status. */
    private Integer enabled;

    /** Creation time. */
    private LocalDateTime createdAt;

    /** Update time. */
    private LocalDateTime updatedAt;
}
