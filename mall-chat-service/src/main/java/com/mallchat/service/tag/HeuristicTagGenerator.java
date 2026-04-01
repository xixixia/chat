package com.mallchat.service.tag;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Simple local heuristic tag generator.
 */
@Component
@Order(100)
public class HeuristicTagGenerator implements TagGenerator {
    private static final Pattern TOKEN_PATTERN = Pattern.compile("[\\p{IsHan}]{2,}|[A-Za-z0-9]{2,}");
    private static final int MAX_TAGS = 5;

    @Override
    public List<String> generate(String title, String content) {
        String source = buildSource(title, content);
        if (source.isEmpty()) {
            return new ArrayList<>();
        }
        LinkedHashSet<String> set = new LinkedHashSet<>();
        Matcher matcher = TOKEN_PATTERN.matcher(source);
        while (matcher.find()) {
            String token = matcher.group();
            if (token == null) {
                continue;
            }
            token = token.trim();
            if (token.isEmpty()) {
                continue;
            }
            if (token.length() > 20) {
                token = token.substring(0, 20);
            }
            set.add(token);
            if (set.size() >= MAX_TAGS) {
                break;
            }
        }
        return new ArrayList<>(set);
    }

    private String buildSource(String title, String content) {
        String t = title == null ? "" : title.trim();
        String c = content == null ? "" : content.trim();
        if (t.isEmpty() && c.isEmpty()) {
            return "";
        }
        return (t + " " + c).toLowerCase(Locale.ROOT);
    }
}
