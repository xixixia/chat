package com.mallchat.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Data;

/**
 * Post creation request.
 */
@Data
public class PostCreateRequest {
    /** Author user ID. */
    private Long userId;

    /** Post title. */
    @NotBlank
    private String title;

    /** Post content. */
    @NotBlank
    private String content;

    /** Category ID. */
    @NotNull
    private Long categoryId;
}
