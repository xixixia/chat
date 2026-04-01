package com.mallchat.service;

import java.util.List;

/**
 * Tag service.
 */
public interface TagService {
    List<String> generateTags(String title, String content);
}
