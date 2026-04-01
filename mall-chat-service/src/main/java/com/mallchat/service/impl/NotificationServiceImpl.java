package com.mallchat.service.impl;

import com.mallchat.common.PageResult;
import com.mallchat.mapper.CommentMapper;
import com.mallchat.mapper.PostMapper;
import com.mallchat.mapper.UserNotificationMapper;
import com.mallchat.model.Comment;
import com.mallchat.model.Post;
import com.mallchat.model.UserNotification;
import com.mallchat.service.NotificationService;
import com.mallchat.vo.NotificationItem;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * Notification service implementation.
 */
@Service
public class NotificationServiceImpl implements NotificationService {
    private static final int SNIPPET_LIMIT = 200;

    private final UserNotificationMapper userNotificationMapper;
    private final PostMapper postMapper;
    private final CommentMapper commentMapper;

    public NotificationServiceImpl(UserNotificationMapper userNotificationMapper,
                                   PostMapper postMapper,
                                   CommentMapper commentMapper) {
        this.userNotificationMapper = userNotificationMapper;
        this.postMapper = postMapper;
        this.commentMapper = commentMapper;
    }

    @Override
    public PageResult<NotificationItem> listByUserId(Long userId, String status, int page, int size) {
        int safePage = Math.max(page, 1);
        int safeSize = Math.min(Math.max(size, 1), 50);
        int offset = (safePage - 1) * safeSize;
        Integer readStatus = parseReadStatus(status);
        List<NotificationItem> list = userNotificationMapper.listByUserId(userId, readStatus, offset, safeSize);
        Long total = userNotificationMapper.countByUserId(userId, readStatus);
        return PageResult.of(total, list);
    }

    @Override
    public Long countUnread(Long userId) {
        return userNotificationMapper.countByUserId(userId, 0);
    }

    @Override
    public boolean markRead(Long userId, List<Long> ids) {
        if (userId == null || ids == null || ids.isEmpty()) {
            return false;
        }
        return userNotificationMapper.markReadByIds(userId, ids) > 0;
    }

    @Override
    public boolean markAllRead(Long userId) {
        if (userId == null) {
            return false;
        }
        return userNotificationMapper.markAllRead(userId) > 0;
    }

    @Override
    public void createForCommentApproved(Comment comment) {
        if (comment == null) {
            return;
        }
        if (comment.getStatus() == null || comment.getStatus() != 1) {
            return;
        }
        if (comment.getAuditStatus() == null || comment.getAuditStatus() != 1) {
            return;
        }
        Long targetUserId = null;
        Long targetId = null;
        String targetType = null;
        String action = null;

        if (comment.getParentId() != null) {
            Comment parent = commentMapper.findById(comment.getParentId());
            if (parent == null || parent.getStatus() == null || parent.getStatus() != 1) {
                return;
            }
            if (parent.getAuditStatus() == null || parent.getAuditStatus() != 1) {
                return;
            }
            targetUserId = parent.getUserId();
            targetId = parent.getId();
            targetType = "comment";
            action = "reply";
        } else {
            Post post = postMapper.findByIdRaw(comment.getPostId());
            if (post == null || post.getStatus() == null || post.getStatus() != 1) {
                return;
            }
            targetUserId = post.getUserId();
            targetId = post.getId();
            targetType = "post";
            action = "comment";
        }

        if (targetUserId == null || targetUserId.equals(comment.getUserId())) {
            return;
        }

        UserNotification notification = new UserNotification();
        notification.setUserId(targetUserId);
        notification.setActorId(comment.getUserId());
        notification.setAction(action);
        notification.setTargetType(targetType);
        notification.setTargetId(targetId);
        notification.setPostId(comment.getPostId());
        notification.setContentSnippet(buildSnippet(comment.getContent()));
        notification.setIsRead(0);
        userNotificationMapper.insert(notification);
    }

    private Integer parseReadStatus(String status) {
        if (status == null) {
            return null;
        }
        String trimmed = status.trim().toLowerCase();
        if (trimmed.isEmpty()) {
            return null;
        }
        if ("unread".equals(trimmed) || "0".equals(trimmed)) {
            return 0;
        }
        if ("read".equals(trimmed) || "1".equals(trimmed)) {
            return 1;
        }
        return null;
    }

    private String buildSnippet(String content) {
        if (content == null) {
            return null;
        }
        String trimmed = content.replaceAll("\\s+", " ").trim();
        if (trimmed.length() <= SNIPPET_LIMIT) {
            return trimmed;
        }
        return trimmed.substring(0, SNIPPET_LIMIT);
    }
}
