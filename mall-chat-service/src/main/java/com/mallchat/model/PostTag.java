package com.mallchat.model;

import java.time.LocalDateTime;
import lombok.Data;

/**
 * Post-tag relation.
 */
@Data
public class PostTag {
    private Long id;
    private Long postId;
    private Long tagId;
    private LocalDateTime createdAt;
}
