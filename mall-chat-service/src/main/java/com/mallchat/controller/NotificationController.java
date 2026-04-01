package com.mallchat.controller;

import com.mallchat.common.PageResult;
import com.mallchat.common.Result;
import com.mallchat.dto.NotificationReadRequest;
import com.mallchat.security.UserContext;
import com.mallchat.service.NotificationService;
import com.mallchat.vo.NotificationItem;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Notification controller.
 */
@RestController
@RequestMapping("/notifications")
public class NotificationController {
    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping
    public Result<PageResult<NotificationItem>> list(
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        Long userId = UserContext.getUserId();
        return Result.ok(notificationService.listByUserId(userId, status, page, size));
    }

    @GetMapping("/unread-count")
    public Result<Long> unreadCount() {
        Long userId = UserContext.getUserId();
        return Result.ok(notificationService.countUnread(userId));
    }

    @PostMapping("/read")
    public Result<Boolean> markRead(@RequestBody NotificationReadRequest request) {
        Long userId = UserContext.getUserId();
        List<Long> ids = request == null ? null : request.getIds();
        return Result.ok(notificationService.markRead(userId, ids));
    }

    @PostMapping("/read/all")
    public Result<Boolean> markAllRead() {
        Long userId = UserContext.getUserId();
        return Result.ok(notificationService.markAllRead(userId));
    }
}
