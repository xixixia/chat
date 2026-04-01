package com.mallchat.moderation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Simple DFA matcher for sensitive words.
 */
public class SensitiveWordMatcher {
    private final Node root = new Node();
    private int maxLen = 0;

    public void addWord(String word, int level) {
        if (word == null) {
            return;
        }
        String normalized = normalize(word);
        if (normalized.isEmpty()) {
            return;
        }
        Node node = root;
        for (int i = 0; i < normalized.length(); i++) {
            char ch = normalized.charAt(i);
            node = node.children.computeIfAbsent(ch, key -> new Node());
        }
        node.end = true;
        node.level = Math.max(node.level, level);
        node.word = normalized;
        maxLen = Math.max(maxLen, normalized.length());
    }

    public List<ModerationHit> match(String text, Set<String> whitelist) {
        if (text == null || text.isEmpty()) {
            return new ArrayList<>();
        }
        String normalized = normalize(text);
        if (normalized.isEmpty()) {
            return new ArrayList<>();
        }
        LinkedHashMap<String, Integer> hitMap = new LinkedHashMap<>();
        int length = normalized.length();
        int maxScan = maxLen > 0 ? maxLen : length;
        for (int i = 0; i < length; i++) {
            Node node = root;
            for (int j = i; j < length && j - i < maxScan; j++) {
                char ch = normalized.charAt(j);
                node = node.children.get(ch);
                if (node == null) {
                    break;
                }
                if (node.end) {
                    String word = node.word;
                    if (word != null && (whitelist == null || !whitelist.contains(word))) {
                        Integer current = hitMap.get(word);
                        if (current == null || node.level > current) {
                            hitMap.put(word, node.level);
                        }
                    }
                }
            }
        }
        if (hitMap.isEmpty()) {
            return new ArrayList<>();
        }
        List<ModerationHit> hits = new ArrayList<>(hitMap.size());
        for (Map.Entry<String, Integer> entry : hitMap.entrySet()) {
            hits.add(new ModerationHit(entry.getKey(), entry.getValue()));
        }
        return hits;
    }

    private String normalize(String input) {
        return input == null ? "" : input.trim().toLowerCase();
    }

    private static final class Node {
        private final Map<Character, Node> children = new HashMap<>();
        private boolean end;
        private int level;
        private String word;
    }
}
