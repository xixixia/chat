package com.mallchat.common;

import java.util.List;
import lombok.Data;

/**
  * Page result wrapper.
  *
  * @param <T> item type
  */
@Data
public class PageResult<T> {
    /** Total count. */
    private Long total;

    /** Current page list. */
    private List<T> list;

    public static <T> PageResult<T> of(Long total, List<T> list) {
        PageResult<T> result = new PageResult<>();
        result.setTotal(total);
        result.setList(list);
        return result;
    }
}
