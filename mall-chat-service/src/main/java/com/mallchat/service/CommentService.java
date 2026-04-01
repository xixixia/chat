package com.mallchat.service;

import com.mallchat.common.PageResult;
import com.mallchat.dto.CommentCreateRequest;
import com.mallchat.model.Comment;
import com.mallchat.vo.ContentCreateResult;
import com.mallchat.vo.CommentItem;
import com.mallchat.vo.MyCommentItem;
import java.util.List;

/**
 * 评论服务。
 */
public interface CommentService {
    /**
     * 根据帖子 ID 查询评论列表。
     *
     * @param postId 帖子 ID
     * @return 评论列表
     */
    List<CommentItem> listByPostId(Long postId);

    /**
     * 根据用户 ID 查询评论列表（分页）。
     *
     * @param userId 用户 ID
     * @param page 页码
     * @param size 每页数量
     * @return 评论列表
     */
    PageResult<MyCommentItem> listByUserId(Long userId, int page, int size);

    /**
     * 创建评论。
     *
     * @param request 创建请求
     * @return 创建结果
     */
    ContentCreateResult create(CommentCreateRequest request);

    /**
     * 根据 ID 查询评论详情。
     *
     * @param id 评论 ID
     * @return 评论详情
     */
    Comment findById(Long id);

    /**
     * 软删除评论。
     *
     * @param id 评论 ID
     * @return 是否删除成功
     */
    boolean deleteById(Long id);
}
