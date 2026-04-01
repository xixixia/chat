package com.mallchat.model;

import java.time.LocalDateTime;
import lombok.Data;

/**
 * Post category entity.
 */
@Data
public class Category {
    private Long id;
    private String name;
    private Long parentId;
    private Integer level;
    private Integer sort;
    private Integer status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
