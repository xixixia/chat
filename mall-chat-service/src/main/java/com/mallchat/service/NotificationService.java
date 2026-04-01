package com.mallchat.service;

import com.mallchat.common.PageResult;
import com.mallchat.model.Comment;
import com.mallchat.vo.NotificationItem;
import java.util.List;

/**
 * Notification service.
 */
public interface NotificationService {
    PageResult<NotificationItem> listByUserId(Long userId, String status, int page, int size);

    Long countUnread(Long userId);

    boolean markRead(Long userId, List<Long> ids);

    boolean markAllRead(Long userId);

    void createForCommentApproved(Comment comment);
}
