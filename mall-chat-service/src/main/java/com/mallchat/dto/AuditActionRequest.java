package com.mallchat.dto;

import lombok.Data;

/**
 * Audit action request.
 */
@Data
public class AuditActionRequest {
    /** Audit reason. */
    private String reason;
}
