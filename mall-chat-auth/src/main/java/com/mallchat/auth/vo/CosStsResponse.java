package com.mallchat.auth.vo;

import lombok.Data;

/**
 * COS STS response.
 */
@Data
public class CosStsResponse {
    private String tmpSecretId;
    private String tmpSecretKey;
    private String sessionToken;
    private Long expiredTime;
    private String bucket;
    private String region;
    private String key;
    private String url;
}
