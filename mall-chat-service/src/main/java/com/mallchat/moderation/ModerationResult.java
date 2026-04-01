package com.mallchat.moderation;

import java.util.Collections;
import java.util.List;
import lombok.Data;

/**
 * Moderation result.
 */
@Data
public class ModerationResult {
    private final ModerationAction action;
    private final int hitLevel;
    private final List<ModerationHit> hits;

    public static ModerationResult pass() {
        return new ModerationResult(ModerationAction.PASS, 0, Collections.emptyList());
    }
}
