package com.mallchat.service.tag;

import java.util.List;

/**
 * Tag generator strategy.
 */
public interface TagGenerator {
    List<String> generate(String title, String content);
}
