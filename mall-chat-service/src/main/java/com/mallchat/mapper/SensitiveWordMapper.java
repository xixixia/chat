package com.mallchat.mapper;

import com.mallchat.model.SensitiveWord;
import java.util.List;

/**
 * Sensitive word mapper.
 */
public interface SensitiveWordMapper {
    /**
     * List enabled sensitive words.
     *
     * @return sensitive words
     */
    List<SensitiveWord> listEnabled();
}
