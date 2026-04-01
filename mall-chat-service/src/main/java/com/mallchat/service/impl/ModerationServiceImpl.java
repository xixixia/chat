package com.mallchat.service.impl;

import com.mallchat.mapper.SensitiveWhitelistMapper;
import com.mallchat.mapper.SensitiveWordMapper;
import com.mallchat.moderation.ModerationAction;
import com.mallchat.moderation.ModerationHit;
import com.mallchat.moderation.ModerationResult;
import com.mallchat.moderation.SensitiveWordMatcher;
import com.mallchat.model.SensitiveWhitelist;
import com.mallchat.model.SensitiveWord;
import com.mallchat.service.ModerationService;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import javax.annotation.PostConstruct;
import org.springframework.stereotype.Service;

/**
 * Moderation service implementation.
 */
@Service
public class ModerationServiceImpl implements ModerationService {
    private final SensitiveWordMapper sensitiveWordMapper;
    private final SensitiveWhitelistMapper whitelistMapper;
    private final AtomicReference<SensitiveWordMatcher> matcherRef =
            new AtomicReference<>(new SensitiveWordMatcher());
    private final AtomicReference<Set<String>> whitelistRef =
            new AtomicReference<>(new HashSet<>());

    public ModerationServiceImpl(SensitiveWordMapper sensitiveWordMapper,
                                 SensitiveWhitelistMapper whitelistMapper) {
        this.sensitiveWordMapper = sensitiveWordMapper;
        this.whitelistMapper = whitelistMapper;
    }

    @PostConstruct
    public void init() {
        refresh();
    }

    @Override
    public ModerationResult check(String text) {
        if (text == null || text.trim().isEmpty()) {
            return ModerationResult.pass();
        }
        SensitiveWordMatcher matcher = matcherRef.get();
        Set<String> whitelist = whitelistRef.get();
        List<ModerationHit> hits = matcher.match(text, whitelist);
        if (hits.isEmpty()) {
            return ModerationResult.pass();
        }
        int maxLevel = 0;
        for (ModerationHit hit : hits) {
            if (hit.getLevel() > maxLevel) {
                maxLevel = hit.getLevel();
            }
        }
        ModerationAction action;
        if (maxLevel >= 3) {
            action = ModerationAction.REJECT;
        } else if (maxLevel == 2) {
            action = ModerationAction.PENDING;
        } else {
            action = ModerationAction.PASS;
        }
        return new ModerationResult(action, maxLevel, hits);
    }

    @Override
    public void refresh() {
        List<SensitiveWord> words = sensitiveWordMapper.listEnabled();
        SensitiveWordMatcher matcher = new SensitiveWordMatcher();
        if (words != null) {
            for (SensitiveWord word : words) {
                if (word == null) {
                    continue;
                }
                int level = word.getLevel() == null ? 1 : word.getLevel();
                matcher.addWord(word.getWord(), level);
            }
        }
        matcherRef.set(matcher);
        List<SensitiveWhitelist> whitelist = whitelistMapper.listEnabled();
        Set<String> whitelistSet = new HashSet<>();
        if (whitelist != null) {
            for (SensitiveWhitelist item : whitelist) {
                if (item == null || item.getWord() == null) {
                    continue;
                }
                whitelistSet.add(item.getWord().trim().toLowerCase());
            }
        }
        whitelistRef.set(whitelistSet);
    }
}
