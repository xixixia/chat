package com.mallchat.model;

import java.time.LocalDateTime;
import lombok.Data;

/**
  * Comment entity.
  */
@Data
public class Comment {
    /** Comment ID. */
    private Long id;

    /** Post ID. */
    private Long postId;

    /** Parent comment ID. */
    private Long parentId;

    /** Author user ID. */
    private Long userId;

    /** Comment content. */
    private String content;

    /** Status: 1 visible, 0 hidden. */
    private Integer status;

    /** Audit status: 0 pending, 1 pass, 2 reject. */
    private Integer auditStatus;

    /** Audit reason. */
    private String auditReason;

    /** Audit update time. */
    private LocalDateTime auditUpdatedAt;

    /** Creation time. */
    private LocalDateTime createdAt;

    /** Last update time. */
    private LocalDateTime updatedAt;
}
