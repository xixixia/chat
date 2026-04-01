package com.mallchat.controller;

import com.mallchat.common.Result;
import com.mallchat.service.CategoryService;
import com.mallchat.vo.CategoryItem;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Category API.
 */
@RestController
@RequestMapping("/categories")
public class CategoryController {
    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public Result<List<CategoryItem>> list() {
        return Result.ok(categoryService.listAll());
    }
}
