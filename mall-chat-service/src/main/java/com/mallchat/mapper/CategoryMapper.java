package com.mallchat.mapper;

import com.mallchat.model.Category;
import com.mallchat.vo.CategoryItem;
import java.util.List;
import org.apache.ibatis.annotations.Param;

/**
 * Category Mapper.
 */
public interface CategoryMapper {
    Category findById(@Param("id") Long id);

    List<CategoryItem> listAll();
}
