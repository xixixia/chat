package com.mallchat.vo;

import lombok.Data;

/**
 * Content creation result.
 */
@Data
public class ContentCreateResult {
    private Long id;
    private Integer auditStatus;
    private String message;
}
