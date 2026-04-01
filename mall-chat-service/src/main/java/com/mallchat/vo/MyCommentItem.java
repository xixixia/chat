package com.mallchat.vo;

import java.time.LocalDateTime;
import lombok.Data;

/**
 * My comment view object.
 */
@Data
public class MyCommentItem {
    /** Comment ID. */
    private Long id;

    /** Post ID. */
    private Long postId;

    /** Post title. */
    private String postTitle;

    /** Parent comment ID. */
    private Long parentId;

    /** Author user ID. */
    private Long userId;

    /** Comment content. */
    private String content;

    /** Audit status: 0 pending, 1 pass, 2 reject. */
    private Integer auditStatus;

    /** Creation time. */
    private LocalDateTime createdAt;
}
