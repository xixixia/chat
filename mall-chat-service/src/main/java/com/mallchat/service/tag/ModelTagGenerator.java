package com.mallchat.service.tag;

import java.util.Collections;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Placeholder for model-based tag generation.
 * If endpoint is not configured, it returns empty.
 */
@Component
@Order(10)
public class ModelTagGenerator implements TagGenerator {
    @Value("${app.tag.model-endpoint:}")
    private String endpoint;

    @Override
    public List<String> generate(String title, String content) {
        if (endpoint == null || endpoint.trim().isEmpty()) {
            return Collections.emptyList();
        }
        // TODO: integrate model API call here.
        return Collections.emptyList();
    }
}
