package com.mallchat.vo;

import lombok.Data;

/**
 * Category item view object.
 */
@Data
public class CategoryItem {
    private Long id;
    private String name;
    private Long parentId;
    private Integer level;
    private Integer sort;
}
