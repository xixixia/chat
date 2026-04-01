package com.mallchat.model;

import java.time.LocalDateTime;
import lombok.Data;

/**
  * Forum post entity.
  */
@Data
public class Post {
    /** Post ID. */
    private Long id;

    /** Author user ID. */
    private Long userId;

    /** Post title. */
    private String title;

    /** Post content. */
    private String content;

    /** Category ID. */
    private Long categoryId;

    /** Status: 1 visible, 0 hidden. */
    private Integer status;

    /** Audit status: 0 pending, 1 pass, 2 reject. */
    private Integer auditStatus;

    /** Audit reason. */
    private String auditReason;

    /** Audit update time. */
    private LocalDateTime auditUpdatedAt;

    /** Like count. */
    private Long likeCount;

    /** Creation time. */
    private LocalDateTime createdAt;

    /** Last update time. */
    private LocalDateTime updatedAt;
}
