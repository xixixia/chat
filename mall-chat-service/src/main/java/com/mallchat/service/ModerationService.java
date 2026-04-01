package com.mallchat.service;

import com.mallchat.moderation.ModerationResult;

/**
 * Moderation service.
 */
public interface ModerationService {
    /**
     * Check text content against sensitive words.
     *
     * @param text content
     * @return moderation result
     */
    ModerationResult check(String text);

    /**
     * Refresh sensitive words cache.
     */
    void refresh();
}
