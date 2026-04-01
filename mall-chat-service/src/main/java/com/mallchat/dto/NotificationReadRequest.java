package com.mallchat.dto;

import java.util.List;
import lombok.Data;

/**
 * Notification read request.
 */
@Data
public class NotificationReadRequest {
    /** Notification IDs to mark as read. */
    private List<Long> ids;
}
