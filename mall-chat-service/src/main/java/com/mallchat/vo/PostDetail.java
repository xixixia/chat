package com.mallchat.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;

/**
  * Post detail view object.
  */
@Data
public class PostDetail {
    /** Post ID. */
    private Long id;

    /** Author user ID. */
    private Long userId;

    /** Post title. */
    private String title;

    /** Post content. */
    private String content;

    /** Category ID. */
    private Long categoryId;

    /** Audit status: 0 pending, 1 pass, 2 reject. */
    private Integer auditStatus;

    /** Category name. */
    private String categoryName;

    /** Tag list. */
    private List<String> tags;

    /** Like count. */
    private Long likeCount;

    /** Whether current user liked this post. */
    private Boolean likedByMe;

    @JsonIgnore
    private String tagNames;

    /** Creation time. */
    private LocalDateTime createdAt;
}
