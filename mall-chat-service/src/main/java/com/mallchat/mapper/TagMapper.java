package com.mallchat.mapper;

import com.mallchat.model.Tag;
import org.apache.ibatis.annotations.Param;

/**
 * Tag Mapper.
 */
public interface TagMapper {
    Tag findByName(@Param("name") String name);

    int insert(Tag tag);
}
