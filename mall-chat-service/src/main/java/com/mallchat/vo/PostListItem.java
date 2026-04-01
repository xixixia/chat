package com.mallchat.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;

/**
  * Post list item view object.
  */
@Data
public class PostListItem {
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

    @JsonIgnore
    private String tagNames;

    /** Creation time. */
    private LocalDateTime createdAt;
}
