package com.mallchat.service.impl;

import com.mallchat.common.PageResult;
import com.mallchat.mapper.CommentMapper;
import com.mallchat.mapper.ContentAuditMapper;
import com.mallchat.mapper.PostMapper;
import com.mallchat.model.Comment;
import com.mallchat.model.ContentAudit;
import com.mallchat.model.Post;
import com.mallchat.moderation.ModerationBizType;
import com.mallchat.moderation.ModerationHit;
import com.mallchat.moderation.ModerationResult;
import com.mallchat.service.ContentAuditService;
import com.mallchat.service.NotificationService;
import com.mallchat.vo.AuditItem;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Content audit service implementation.
 */
@Service
public class ContentAuditServiceImpl implements ContentAuditService {
    private final ContentAuditMapper contentAuditMapper;
    private final PostMapper postMapper;
    private final CommentMapper commentMapper;
    private final NotificationService notificationService;

    public ContentAuditServiceImpl(ContentAuditMapper contentAuditMapper,
                                   PostMapper postMapper,
                                   CommentMapper commentMapper,
                                   NotificationService notificationService) {
        this.contentAuditMapper = contentAuditMapper;
        this.postMapper = postMapper;
        this.commentMapper = commentMapper;
        this.notificationService = notificationService;
    }

    @Override
    public void recordAutoAudit(String bizType, Long bizId, ModerationResult result, Integer status, String reason) {
        if (bizId == null || result == null) {
            return;
        }
        List<ModerationHit> hits = result.getHits();
        boolean hasHits = hits != null && !hits.isEmpty();
        if (!hasHits && Objects.equals(status, 1)) {
            return;
        }
        ContentAudit audit = new ContentAudit();
        audit.setBizType(bizType);
        audit.setBizId(bizId);
        audit.setStatus(status);
        audit.setHitLevel(result.getHitLevel());
        audit.setHitWords(joinHits(hits));
        audit.setReason(reason);
        contentAuditMapper.insert(audit);
    }

    @Override
    public PageResult<AuditItem> list(Integer status, int page, int size) {
        int safePage = Math.max(page, 1);
        int safeSize = Math.min(Math.max(size, 1), 50);
        int offset = (safePage - 1) * safeSize;
        List<ContentAudit> audits = contentAuditMapper.listByStatus(status, offset, safeSize);
        List<AuditItem> items = new ArrayList<>();
        if (audits != null) {
            for (ContentAudit audit : audits) {
                AuditItem item = new AuditItem();
                item.setId(audit.getId());
                item.setBizType(audit.getBizType());
                item.setBizId(audit.getBizId());
                item.setStatus(audit.getStatus());
                item.setHitLevel(audit.getHitLevel());
                item.setHitWords(audit.getHitWords());
                item.setOperatorId(audit.getOperatorId());
                item.setReason(audit.getReason());
                item.setCreatedAt(audit.getCreatedAt());
                item.setUpdatedAt(audit.getUpdatedAt());
                fillContent(item);
                items.add(item);
            }
        }
        Long total = contentAuditMapper.countByStatus(status);
        return PageResult.of(total, items);
    }

    @Override
    @Transactional
    public boolean approve(Long id, Long operatorId, String reason) {
        ContentAudit audit = contentAuditMapper.findById(id);
        if (audit == null) {
            return false;
        }
        boolean shouldNotify = Objects.equals(audit.getStatus(), 0);
        if (contentAuditMapper.updateStatus(id, 1, reason, operatorId) <= 0) {
            return false;
        }
        updateBizAudit(audit.getBizType(), audit.getBizId(), 1, reason);
        if (shouldNotify && ModerationBizType.COMMENT.equals(audit.getBizType())) {
            Comment comment = commentMapper.findById(audit.getBizId());
            notificationService.createForCommentApproved(comment);
        }
        return true;
    }

    @Override
    @Transactional
    public boolean reject(Long id, Long operatorId, String reason) {
        ContentAudit audit = contentAuditMapper.findById(id);
        if (audit == null) {
            return false;
        }
        if (contentAuditMapper.updateStatus(id, 2, reason, operatorId) <= 0) {
            return false;
        }
        updateBizAudit(audit.getBizType(), audit.getBizId(), 2, reason);
        return true;
    }

    private void updateBizAudit(String bizType, Long bizId, int status, String reason) {
        if (bizId == null || bizType == null) {
            return;
        }
        if (ModerationBizType.POST.equals(bizType)) {
            postMapper.updateAuditStatus(bizId, status, reason);
        } else if (ModerationBizType.COMMENT.equals(bizType)) {
            commentMapper.updateAuditStatus(bizId, status, reason);
        }
    }

    private void fillContent(AuditItem item) {
        if (item == null || item.getBizId() == null) {
            return;
        }
        if (ModerationBizType.POST.equals(item.getBizType())) {
            Post post = postMapper.findByIdRaw(item.getBizId());
            if (post != null) {
                item.setTitle(post.getTitle());
                item.setContent(post.getContent());
            }
        } else if (ModerationBizType.COMMENT.equals(item.getBizType())) {
            Comment comment = commentMapper.findById(item.getBizId());
            if (comment != null) {
                item.setContent(comment.getContent());
            }
        }
    }

    private String joinHits(List<ModerationHit> hits) {
        if (hits == null || hits.isEmpty()) {
            return null;
        }
        StringJoiner joiner = new StringJoiner(",");
        for (ModerationHit hit : hits) {
            if (hit.getWord() != null && !hit.getWord().isEmpty()) {
                joiner.add(hit.getWord());
            }
        }
        String words = joiner.toString();
        return words.isEmpty() ? null : words;
    }
}
