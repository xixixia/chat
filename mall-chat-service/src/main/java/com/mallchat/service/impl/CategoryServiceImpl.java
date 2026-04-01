package com.mallchat.service.impl;

import com.mallchat.mapper.CategoryMapper;
import com.mallchat.service.CategoryService;
import com.mallchat.vo.CategoryItem;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * Category service implementation.
 */
@Service
public class CategoryServiceImpl implements CategoryService {
    private final CategoryMapper categoryMapper;

    public CategoryServiceImpl(CategoryMapper categoryMapper) {
        this.categoryMapper = categoryMapper;
    }

    @Override
    public List<CategoryItem> listAll() {
        return categoryMapper.listAll();
    }
}
