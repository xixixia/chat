package com.mallchat.mapper;

import com.mallchat.model.SensitiveWhitelist;
import java.util.List;

/**
 * Sensitive whitelist mapper.
 */
public interface SensitiveWhitelistMapper {
    /**
     * List enabled whitelist words.
     *
     * @return whitelist words
     */
    List<SensitiveWhitelist> listEnabled();
}
