package com.mallchat.model;

import java.time.LocalDateTime;
import lombok.Data;

/**
 * Content audit entity.
 */
@Data
public class ContentAudit {
    /** ID. */
    private Long id;

    /** Business type: post/comment. */
    private String bizType;

    /** Business ID. */
    private Long bizId;

    /** Audit status: 0 pending, 1 pass, 2 reject. */
    private Integer status;

    /** Hit level: 0 none, 1 low, 2 medium, 3 high. */
    private Integer hitLevel;

    /** Hit words (comma separated). */
    private String hitWords;

    /** Operator user ID. */
    private Long operatorId;

    /** Audit reason. */
    private String reason;

    /** Creation time. */
    private LocalDateTime createdAt;

    /** Update time. */
    private LocalDateTime updatedAt;
}
