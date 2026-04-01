package com.mallchat.model;

import java.time.LocalDateTime;
import lombok.Data;

/**
 * Tag entity.
 */
@Data
public class Tag {
    private Long id;
    private String name;
    private Integer status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
