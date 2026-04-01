package com.mallchat.service;

import com.mallchat.vo.CategoryItem;
import java.util.List;

/**
 * Category service.
 */
public interface CategoryService {
    List<CategoryItem> listAll();
}
