package com.mallchat.service;

import com.mallchat.common.PageResult;
import com.mallchat.moderation.ModerationResult;
import com.mallchat.vo.AuditItem;

/**
 * Content audit service.
 */
public interface ContentAuditService {
    /**
     * Record automated audit.
     *
     * @param bizType business type
     * @param bizId business id
     * @param result moderation result
     * @param status audit status
     * @param reason audit reason
     */
    void recordAutoAudit(String bizType, Long bizId, ModerationResult result, Integer status, String reason);

    /**
     * List audits.
     *
     * @param status status filter
     * @param page page
     * @param size size
     * @return page result
     */
    PageResult<AuditItem> list(Integer status, int page, int size);

    /**
     * Approve audit.
     *
     * @param id audit id
     * @param operatorId operator id
     * @param reason reason
     * @return updated
     */
    boolean approve(Long id, Long operatorId, String reason);

    /**
     * Reject audit.
     *
     * @param id audit id
     * @param operatorId operator id
     * @param reason reason
     * @return updated
     */
    boolean reject(Long id, Long operatorId, String reason);
}
