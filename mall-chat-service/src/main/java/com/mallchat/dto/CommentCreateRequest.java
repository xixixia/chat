package com.mallchat.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Data;

/**
 * Comment creation request.
 */
@Data
public class CommentCreateRequest {
    /** Post ID. */
    @NotNull
    private Long postId;

    /** Parent comment ID. */
    private Long parentId;

    /** Author user ID. */
    private Long userId;

    /** Comment content. */
    @NotBlank
    private String content;
}
