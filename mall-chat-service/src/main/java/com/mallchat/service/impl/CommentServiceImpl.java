package com.mallchat.service.impl;

import com.mallchat.common.PageResult;
import com.mallchat.dto.CommentCreateRequest;
import com.mallchat.exception.BusinessException;
import com.mallchat.mapper.CommentMapper;
import com.mallchat.model.Comment;
import com.mallchat.moderation.ModerationAction;
import com.mallchat.moderation.ModerationBizType;
import com.mallchat.moderation.ModerationResult;
import com.mallchat.service.ContentAuditService;
import com.mallchat.service.CommentService;
import com.mallchat.service.ModerationService;
import com.mallchat.service.NotificationService;
import com.mallchat.vo.ContentCreateResult;
import com.mallchat.vo.CommentItem;
import com.mallchat.vo.MyCommentItem;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedHashSet;
import java.util.List;
import java.time.LocalDateTime;
import org.springframework.stereotype.Service;

/**
 * 评论服务实现。
 */
@Service
public class CommentServiceImpl implements CommentService {
    private final CommentMapper commentMapper;
    private final ModerationService moderationService;
    private final ContentAuditService contentAuditService;
    private final NotificationService notificationService;

    public CommentServiceImpl(CommentMapper commentMapper,
                              ModerationService moderationService,
                              ContentAuditService contentAuditService,
                              NotificationService notificationService) {
        this.commentMapper = commentMapper;
        this.moderationService = moderationService;
        this.contentAuditService = contentAuditService;
        this.notificationService = notificationService;
    }

    /**
     * 根据帖子 ID 查询评论列表。
     *
     * @param postId 帖子 ID
     * @return 评论列表
     */
    @Override
    public List<CommentItem> listByPostId(Long postId) {
        return commentMapper.listByPostId(postId);
    }

    /**
     * 根据用户 ID 查询评论列表（分页）。
     *
     * @param userId 用户 ID
     * @param page 页码
     * @param size 每页数量
     * @return 评论列表
     */
    @Override
    public PageResult<MyCommentItem> listByUserId(Long userId, int page, int size) {
        int safePage = Math.max(page, 1);
        int safeSize = Math.min(Math.max(size, 1), 50);
        int offset = (safePage - 1) * safeSize;
        List<MyCommentItem> list = commentMapper.listByUserId(userId, offset, safeSize);
        Long total = commentMapper.countByUserId(userId);
        return PageResult.of(total, list);
    }

    /**
     * 创建评论。
     *
     * @param request 创建请求
     * @return 创建结果
     */
    @Override
    public ContentCreateResult create(CommentCreateRequest request) {
        if (request.getParentId() != null) {
            Comment parent = commentMapper.findById(request.getParentId());
            if (parent == null || parent.getStatus() == null || parent.getStatus() == 0) {
                throw new BusinessException("父评论不存在");
            }
            if (parent.getAuditStatus() != null && parent.getAuditStatus() != 1) {
                throw new BusinessException("父评论未通过审核");
            }
            if (!parent.getPostId().equals(request.getPostId())) {
                throw new BusinessException("父评论与帖子不匹配");
            }
        }
        ModerationResult moderation = moderationService.check(request.getContent());
        if (moderation.getAction() == ModerationAction.REJECT) {
            throw new BusinessException("内容包含敏感词，已拒绝发布");
        }
        int auditStatus = moderation.getAction() == ModerationAction.PENDING ? 0 : 1;
        String auditReason = moderation.getAction() == ModerationAction.PENDING
                ? "命中敏感词，待审核"
                : moderation.getHitLevel() > 0 ? "命中低危词，自动通过" : null;
        Comment comment = new Comment();
        comment.setPostId(request.getPostId());
        comment.setParentId(request.getParentId());
        comment.setUserId(request.getUserId());
        comment.setContent(request.getContent());
        comment.setStatus(1);
        comment.setAuditStatus(auditStatus);
        comment.setAuditReason(auditReason);
        if (moderation.getHitLevel() > 0 || moderation.getAction() != ModerationAction.PASS) {
            comment.setAuditUpdatedAt(LocalDateTime.now());
        }
        commentMapper.insert(comment);
        contentAuditService.recordAutoAudit(
                ModerationBizType.COMMENT,
                comment.getId(),
                moderation,
                auditStatus,
                auditReason);
        if (auditStatus == 1) {
            notificationService.createForCommentApproved(comment);
        }
        ContentCreateResult result = new ContentCreateResult();
        result.setId(comment.getId());
        result.setAuditStatus(auditStatus);
        result.setMessage(auditStatus == 1 ? "评论成功" : "评论已提交审核，审核通过后可见");
        return result;
    }

    /**
     * 根据 ID 查询评论详情。
     *
     * @param id 评论 ID
     * @return 评论详情
     */
    @Override
    public Comment findById(Long id) {
        return commentMapper.findById(id);
    }

    /**
     * 软删除评论。
     *
     * @param id 评论 ID
     * @return 是否删除成功
     */
    @Override
    public boolean deleteById(Long id) {
        if (id == null) {
            return false;
        }
        LinkedHashSet<Long> allIds = new LinkedHashSet<>();
        Deque<Long> queue = new ArrayDeque<>();
        allIds.add(id);
        queue.add(id);

        while (!queue.isEmpty()) {
            List<Long> batch = new ArrayList<>();
            while (!queue.isEmpty()) {
                batch.add(queue.poll());
            }
            if (batch.isEmpty()) {
                break;
            }
            List<Long> children = commentMapper.listChildIds(batch);
            if (children != null && !children.isEmpty()) {
                for (Long childId : children) {
                    if (childId != null && allIds.add(childId)) {
                        queue.add(childId);
                    }
                }
            }
        }

        if (allIds.isEmpty()) {
            return false;
        }
        return commentMapper.deleteByIds(new ArrayList<>(allIds)) > 0;
    }
}
