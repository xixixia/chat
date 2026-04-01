package com.mallchat.service.impl;

import com.mallchat.common.PageResult;
import com.mallchat.dto.PostCreateRequest;
import com.mallchat.exception.BusinessException;
import com.mallchat.mapper.CategoryMapper;
import com.mallchat.mapper.PostTagMapper;
import com.mallchat.mapper.PostMapper;
import com.mallchat.mapper.TagMapper;
import com.mallchat.model.Category;
import com.mallchat.model.Post;
import com.mallchat.model.PostTag;
import com.mallchat.model.Tag;
import com.mallchat.moderation.ModerationAction;
import com.mallchat.moderation.ModerationBizType;
import com.mallchat.moderation.ModerationResult;
import com.mallchat.service.ContentAuditService;
import com.mallchat.service.LikeService;
import com.mallchat.service.ModerationService;
import com.mallchat.service.PostService;
import com.mallchat.service.TagService;
import com.mallchat.vo.ContentCreateResult;
import com.mallchat.vo.PostDetail;
import com.mallchat.vo.PostListItem;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * 帖子服务实现。
 */
@Service
public class PostServiceImpl implements PostService {
    private final PostMapper postMapper;
    private final CategoryMapper categoryMapper;
    private final TagMapper tagMapper;
    private final PostTagMapper postTagMapper;
    private final TagService tagService;
    private final LikeService likeService;
    private final ModerationService moderationService;
    private final ContentAuditService contentAuditService;

    public PostServiceImpl(PostMapper postMapper,
                           CategoryMapper categoryMapper,
                           TagMapper tagMapper,
                           PostTagMapper postTagMapper,
                           TagService tagService,
                           LikeService likeService,
                           ModerationService moderationService,
                           ContentAuditService contentAuditService) {
        this.postMapper = postMapper;
        this.categoryMapper = categoryMapper;
        this.tagMapper = tagMapper;
        this.postTagMapper = postTagMapper;
        this.tagService = tagService;
        this.likeService = likeService;
        this.moderationService = moderationService;
        this.contentAuditService = contentAuditService;
    }

    /**
     * 分页查询帖子。
     *
     * @param page 页码
     * @param size 每页数量
     * @param title 标题关键词（可选）
     * @return 分页结果
     */
    @Override
    public PageResult<PostListItem> list(int page, int size, String title, Long categoryId, Long currentUserId) {
        int safePage = Math.max(page, 1);
        int safeSize = Math.min(Math.max(size, 1), 50);
        int offset = (safePage - 1) * safeSize;
        String keyword = title == null ? null : title.trim();
        if (keyword != null && keyword.isEmpty()) {
            keyword = null;
        }
        List<PostListItem> list = postMapper.list(offset, safeSize, keyword, categoryId);
        applyTagNames(list);
        likeService.fillPostLikeInfo(list, currentUserId);
        Long total = postMapper.countByTitle(keyword, categoryId);
        return PageResult.of(total, list);
    }

    /**
     * 根据用户 ID 分页查询帖子。
     *
     * @param userId 用户 ID
     * @param page 页码
     * @param size 每页数量
     * @return 分页结果
     */
    @Override
    public PageResult<PostListItem> listByUserId(Long userId, int page, int size, Long currentUserId) {
        int safePage = Math.max(page, 1);
        int safeSize = Math.min(Math.max(size, 1), 50);
        int offset = (safePage - 1) * safeSize;
        List<PostListItem> list = postMapper.listByUserId(userId, offset, safeSize);
        applyTagNames(list);
        likeService.fillPostLikeInfo(list, currentUserId);
        Long total = postMapper.countByUserId(userId);
        return PageResult.of(total, list);
    }

    /**
     * 创建帖子。
     *
     * @param request 创建请求
     * @return 创建结果
     */
    @Override
    public ContentCreateResult create(PostCreateRequest request) {
        Category category = categoryMapper.findById(request.getCategoryId());
        if (category == null || category.getStatus() == null || category.getStatus() != 1) {
            throw new BusinessException("分类不存在");
        }
        if (category.getLevel() != null && category.getLevel() > 3) {
            throw new BusinessException("分类层级过深");
        }
        ModerationResult moderation = moderationService.check(buildPostContent(request));
        if (moderation.getAction() == ModerationAction.REJECT) {
            throw new BusinessException("内容包含敏感词，已拒绝发布");
        }
        int auditStatus = moderation.getAction() == ModerationAction.PENDING ? 0 : 1;
        String auditReason = moderation.getAction() == ModerationAction.PENDING
                ? "命中敏感词，待审核"
                : moderation.getHitLevel() > 0 ? "命中低危词，自动通过" : null;
        Post post = new Post();
        post.setUserId(request.getUserId());
        post.setCategoryId(request.getCategoryId());
        post.setTitle(request.getTitle());
        post.setContent(request.getContent());
        post.setStatus(1);
        post.setAuditStatus(auditStatus);
        post.setAuditReason(auditReason);
        if (moderation.getHitLevel() > 0 || moderation.getAction() != ModerationAction.PASS) {
            post.setAuditUpdatedAt(LocalDateTime.now());
        }
        postMapper.insert(post);
        bindTags(post.getId(), request.getTitle(), request.getContent());
        contentAuditService.recordAutoAudit(
                ModerationBizType.POST,
                post.getId(),
                moderation,
                auditStatus,
                auditReason);
        ContentCreateResult result = new ContentCreateResult();
        result.setId(post.getId());
        result.setAuditStatus(auditStatus);
        result.setMessage(auditStatus == 1 ? "发布成功" : "内容已提交审核，审核通过后可见");
        return result;
    }

    /**
     * 根据 ID 查询帖子详情。
     *
     * @param id 帖子 ID
     * @return 帖子详情
     */
    @Override
    public PostDetail findById(Long id, Long currentUserId) {
        PostDetail detail = postMapper.findById(id);
        applyTagNames(detail);
        likeService.fillPostLikeInfo(detail, currentUserId);
        return detail;
    }

    @Override
    public Post findByIdRaw(Long id) {
        return postMapper.findByIdRaw(id);
    }

    /**
     * 软删除帖子。
     *
     * @param id 帖子 ID
     * @return 是否删除成功
     */
    @Override
    public boolean deleteById(Long id) {
        return postMapper.deleteById(id) > 0;
    }

    private void bindTags(Long postId, String title, String content) {
        if (postId == null) {
            return;
        }
        List<String> tags = tagService.generateTags(title, content);
        if (tags == null || tags.isEmpty()) {
            return;
        }
        List<PostTag> items = new ArrayList<>();
        for (String raw : tags) {
            String name = raw == null ? null : raw.trim();
            if (name == null || name.isEmpty()) {
                continue;
            }
            Tag tag = tagMapper.findByName(name);
            if (tag == null) {
                tag = new Tag();
                tag.setName(name);
                tag.setStatus(1);
                tagMapper.insert(tag);
            }
            PostTag pt = new PostTag();
            pt.setPostId(postId);
            pt.setTagId(tag.getId());
            items.add(pt);
        }
        if (!items.isEmpty()) {
            postTagMapper.insertBatch(items);
        }
    }

    private void applyTagNames(List<PostListItem> list) {
        if (list == null) {
            return;
        }
        for (PostListItem item : list) {
            applyTagNames(item);
        }
    }

    private void applyTagNames(PostDetail detail) {
        if (detail == null) {
            return;
        }
        detail.setTags(splitTags(detail.getTagNames()));
    }

    private void applyTagNames(PostListItem item) {
        if (item == null) {
            return;
        }
        item.setTags(splitTags(item.getTagNames()));
    }

    private List<String> splitTags(String tagNames) {
        List<String> tags = new ArrayList<>();
        if (tagNames == null || tagNames.trim().isEmpty()) {
            return tags;
        }
        String[] parts = tagNames.split(",");
        for (String part : parts) {
            String name = part == null ? "" : part.trim();
            if (!name.isEmpty()) {
                tags.add(name);
            }
        }
        return tags;
    }

    private String buildPostContent(PostCreateRequest request) {
        String title = request.getTitle() == null ? "" : request.getTitle();
        String content = request.getContent() == null ? "" : request.getContent();
        return title + "\n" + content;
    }
}
