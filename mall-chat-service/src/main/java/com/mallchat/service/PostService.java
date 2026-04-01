package com.mallchat.service;

import com.mallchat.common.PageResult;
import com.mallchat.dto.PostCreateRequest;
import com.mallchat.model.Post;
import com.mallchat.vo.ContentCreateResult;
import com.mallchat.vo.PostDetail;
import com.mallchat.vo.PostListItem;

/**
 * 帖子服务。
 */
public interface PostService {
    /**
     * 分页查询帖子。
     *
     * @param page 页码
     * @param size 每页数量
     * @param title 标题关键词（可选）
     * @return 分页结果
     */
    PageResult<PostListItem> list(int page, int size, String title, Long categoryId);

    /**
     * 根据用户 ID 分页查询帖子。
     *
     * @param userId 用户 ID
     * @param page 页码
     * @param size 每页数量
     * @return 分页结果
     */
    PageResult<PostListItem> listByUserId(Long userId, int page, int size);

    /**
     * 创建帖子。
     *
     * @param request 创建请求
     * @return 创建结果
     */
    ContentCreateResult create(PostCreateRequest request);

    /**
     * 根据 ID 查询帖子实体（不限制审核状态）。
     *
     * @param id 帖子 ID
     * @return 帖子实体
     */
    Post findByIdRaw(Long id);

    /**
     * 根据 ID 查询帖子详情。
     *
     * @param id 帖子 ID
     * @return 帖子详情
     */
    PostDetail findById(Long id);

    /**
     * 软删除帖子。
     *
     * @param id 帖子 ID
     * @return 是否删除成功
     */
    boolean deleteById(Long id);
}
