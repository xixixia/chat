package com.mallchat.vo;

import java.time.LocalDateTime;
import lombok.Data;

/**
 * Notification item.
 */
@Data
public class NotificationItem {
    private Long id;
    private Long userId;
    private Long actorId;
    private String actorNickname;
    private String action;
    private String targetType;
    private Long targetId;
    private Long postId;
    private String contentSnippet;
    private Integer isRead;
    private LocalDateTime createdAt;
}
