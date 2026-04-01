package com.mallchat.vo;

import java.time.LocalDateTime;
import lombok.Data;

/**
  * Comment view object.
  */
@Data
public class CommentItem {
    /** Comment ID. */
    private Long id;

    /** Post ID. */
    private Long postId;

    /** Parent comment ID. */
    private Long parentId;

    /** Author user ID. */
    private Long userId;

    /** Comment content. */
    private String content;

    /** Creation time. */
    private LocalDateTime createdAt;
}
