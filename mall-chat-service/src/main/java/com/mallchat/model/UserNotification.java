package com.mallchat.model;

import java.time.LocalDateTime;
import lombok.Data;

/**
 * User notification entity.
 */
@Data
public class UserNotification {
    /** Notification ID. */
    private Long id;

    /** Recipient user ID. */
    private Long userId;

    /** Actor user ID. */
    private Long actorId;

    /** Action: comment/reply. */
    private String action;

    /** Target type: post/comment. */
    private String targetType;

    /** Target ID. */
    private Long targetId;

    /** Post ID. */
    private Long postId;

    /** Snippet of content. */
    private String contentSnippet;

    /** Read flag: 0 unread, 1 read. */
    private Integer isRead;

    /** Read time. */
    private LocalDateTime readAt;

    /** Creation time. */
    private LocalDateTime createdAt;
}
