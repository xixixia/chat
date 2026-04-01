package com.mallchat.vo;

import java.time.LocalDateTime;
import lombok.Data;

/**
 * Audit list item.
 */
@Data
public class AuditItem {
    private Long id;
    private String bizType;
    private Long bizId;
    private Integer status;
    private Integer hitLevel;
    private String hitWords;
    private Long operatorId;
    private String reason;
    private String title;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
