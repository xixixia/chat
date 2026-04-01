package com.mallchat.mapper;

import com.mallchat.model.Comment;
import com.mallchat.vo.CommentItem;
import com.mallchat.vo.MyCommentItem;
import java.util.List;
import org.apache.ibatis.annotations.Param;

/**
 * 评论 Mapper。
 */
public interface CommentMapper {
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
     * @param offset 偏移量
     * @param limit 每页数量
     * @return 评论列表
     */
    List<MyCommentItem> listByUserId(@Param("userId") Long userId,
                                     @Param("offset") int offset,
                                     @Param("limit") int limit);

    /**
     * 统计用户评论总数。
     *
     * @param userId 用户 ID
     * @return 总数
     */
    Long countByUserId(@Param("userId") Long userId);

    /**
     * 新增评论。
     *
     * @param comment 评论实体
     * @return 影响行数
     */
    int insert(Comment comment);

    /**
     * 根据 ID 查询评论详情。
     *
     * @param id 评论 ID
     * @return 评论详情
     */
    Comment findById(@Param("id") Long id);

    /**
     * 软删除评论。
     *
     * @param id 评论 ID
     * @return 影响行数
     */
    int deleteById(@Param("id") Long id);

    /**
     * 查询子评论 ID 列表。
     *
     * @param parentIds 父评论 ID 列表
     * @return 子评论 ID 列表
     */
    List<Long> listChildIds(@Param("parentIds") List<Long> parentIds);

    /**
     * 批量软删除评论。
     *
     * @param ids 评论 ID 列表
     * @return 影响行数
     */
    int deleteByIds(@Param("ids") List<Long> ids);

    /**
     * Update audit status.
     *
     * @param id comment ID
     * @param auditStatus audit status
     * @param auditReason audit reason
     * @return rows affected
     */
    int updateAuditStatus(@Param("id") Long id,
                          @Param("auditStatus") Integer auditStatus,
                          @Param("auditReason") String auditReason);
}
