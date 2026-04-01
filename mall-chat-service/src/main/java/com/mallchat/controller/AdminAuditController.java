package com.mallchat.controller;

import com.mallchat.common.PageResult;
import com.mallchat.common.Result;
import com.mallchat.dto.AuditActionRequest;
import com.mallchat.exception.ForbiddenException;
import com.mallchat.security.UserContext;
import com.mallchat.service.ContentAuditService;
import com.mallchat.vo.AuditItem;
import java.util.HashSet;
import java.util.Set;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Admin audit controller.
 */
@RestController
@RequestMapping("/admin/audit")
public class AdminAuditController {
    private final ContentAuditService contentAuditService;

    @Value("${app.moderation.admin-user-ids:1}")
    private String adminUserIds;

    private final Set<Long> adminIdSet = new HashSet<>();

    public AdminAuditController(ContentAuditService contentAuditService) {
        this.contentAuditService = contentAuditService;
    }

    @PostConstruct
    public void init() {
        adminIdSet.clear();
        if (adminUserIds == null || adminUserIds.trim().isEmpty()) {
            return;
        }
        String[] parts = adminUserIds.split(",");
        for (String part : parts) {
            String trimmed = part.trim();
            if (trimmed.isEmpty()) {
                continue;
            }
            try {
                adminIdSet.add(Long.valueOf(trimmed));
            } catch (NumberFormatException ignore) {
                // ignore invalid values
            }
        }
    }

    @GetMapping
    public Result<PageResult<AuditItem>> list(
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        ensureAdmin();
        return Result.ok(contentAuditService.list(status, page, size));
    }

    @PostMapping("/{id}/pass")
    public Result<Boolean> pass(@PathVariable Long id, @RequestBody(required = false) AuditActionRequest request) {
        ensureAdmin();
        Long operatorId = UserContext.getUserId();
        String reason = request == null ? null : request.getReason();
        return Result.ok(contentAuditService.approve(id, operatorId, reason));
    }

    @PostMapping("/{id}/reject")
    public Result<Boolean> reject(@PathVariable Long id, @RequestBody(required = false) AuditActionRequest request) {
        ensureAdmin();
        Long operatorId = UserContext.getUserId();
        String reason = request == null ? null : request.getReason();
        return Result.ok(contentAuditService.reject(id, operatorId, reason));
    }

    private void ensureAdmin() {
        Long userId = UserContext.getUserId();
        if (userId == null || !adminIdSet.contains(userId)) {
            throw new ForbiddenException("无权限执行审核操作");
        }
    }
}
