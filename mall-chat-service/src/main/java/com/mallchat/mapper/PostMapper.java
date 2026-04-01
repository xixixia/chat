package com.mallchat.mapper;

import com.mallchat.model.Post;
import com.mallchat.vo.PostDetail;
import com.mallchat.vo.PostListItem;
import java.util.List;
import org.apache.ibatis.annotations.Param;

/**
 * 帖子 Mapper。
 */
public interface PostMapper {
    /**
     * 分页查询帖子。
     *
     * @param offset 偏移量
     * @param limit 每页数量
     * @return 帖子列表
     */
    List<PostListItem> list(
            @Param("offset") int offset,
            @Param("limit") int limit,
            @Param("title") String title,
            @Param("categoryId") Long categoryId);

    /**
     * 根据用户 ID 分页查询帖子。
     *
     * @param userId 用户 ID
     * @param offset 偏移量
     * @param limit 每页数量
     * @return 帖子列表
     */
    List<PostListItem> listByUserId(
            @Param("userId") Long userId,
            @Param("offset") int offset,
            @Param("limit") int limit);

    /**
     * 统计帖子总数。
     *
     * @return 总数
     */
    Long countByTitle(@Param("title") String title, @Param("categoryId") Long categoryId);

    /**
     * 统计用户帖子总数。
     *
     * @param userId 用户 ID
     * @return 总数
     */
    Long countByUserId(@Param("userId") Long userId);

    /**
     * 根据 ID 查询帖子详情。
     *
     * @param id 帖子 ID
     * @return 帖子详情
     */
    PostDetail findById(@Param("id") Long id);

    /**
     * Query post entity by ID (no audit filter).
     *
     * @param id post ID
     * @return post entity
     */
    Post findByIdRaw(@Param("id") Long id);

    /**
     * 新增帖子。
     *
     * @param post 帖子实体
     * @return 影响行数
     */
    int insert(Post post);

    /**
     * 软删除帖子。
     *
     * @param id 帖子 ID
     * @return 影响行数
     */
    int deleteById(@Param("id") Long id);

    /**
     * Update audit status.
     *
     * @param id post ID
     * @param auditStatus audit status
     * @param auditReason audit reason
     * @return rows affected
     */
    int updateAuditStatus(@Param("id") Long id,
                          @Param("auditStatus") Integer auditStatus,
                          @Param("auditReason") String auditReason);

    /**
     * Increase post like count by 1.
     *
     * @param id post ID
     * @return rows affected
     */
    int increaseLikeCount(@Param("id") Long id);

    /**
     * Decrease post like count by 1 (not lower than 0).
     *
     * @param id post ID
     * @return rows affected
     */
    int decreaseLikeCount(@Param("id") Long id);

    /**
     * Query post like count by ID.
     *
     * @param id post ID
     * @return like count
     */
    Long findLikeCountById(@Param("id") Long id);
}
