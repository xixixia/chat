package com.mallchat.auth.vo;

import java.util.List;
import lombok.Data;

/**
 * COS upload config.
 */
@Data
public class CosUploadConfigResponse {
    private List<String> allowedTypes;
    private Integer maxSizeMb;
}
