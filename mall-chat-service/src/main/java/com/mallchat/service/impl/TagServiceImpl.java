package com.mallchat.service.impl;

import com.mallchat.service.TagService;
import com.mallchat.service.tag.TagGenerator;
import java.util.Collections;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * Tag service with generator chain.
 */
@Service
public class TagServiceImpl implements TagService {
    private final List<TagGenerator> generators;

    public TagServiceImpl(List<TagGenerator> generators) {
        this.generators = generators == null ? Collections.emptyList() : generators;
    }

    @Override
    public List<String> generateTags(String title, String content) {
        for (TagGenerator generator : generators) {
            try {
                List<String> tags = generator.generate(title, content);
                if (tags != null && !tags.isEmpty()) {
                    return tags;
                }
            } catch (Exception ignored) {
                // fallback to next generator
            }
        }
        return Collections.emptyList();
    }
}
