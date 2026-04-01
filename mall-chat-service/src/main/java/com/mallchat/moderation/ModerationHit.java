package com.mallchat.moderation;

import lombok.Data;

/**
 * Moderation hit.
 */
@Data
public class ModerationHit {
    private final String word;
    private final int level;
}
