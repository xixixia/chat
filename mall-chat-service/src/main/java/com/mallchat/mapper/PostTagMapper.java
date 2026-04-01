package com.mallchat.mapper;

import com.mallchat.model.PostTag;
import java.util.List;
import org.apache.ibatis.annotations.Param;

/**
 * Post tag mapper.
 */
public interface PostTagMapper {
    int insertBatch(@Param("items") List<PostTag> items);
}
