package com.mallchat.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 签到记录实体。
 */
@Data
public class SignRecord {
    /** 记录 ID。 */
    private Long id;

    /** 用户 ID。 */
    private Long userId;

    /** 签到日期（yyyy-MM-dd）。 */
    private LocalDate signDate;

    /** 签到时间。 */
    private LocalDateTime signTime;

    /** 是否补签：1 是，0 否。 */
    private Integer isRetro;

    /** 奖励状态：0 待发放，1 已发放。 */
    private Integer rewardStatus;

    /** 创建时间。 */
    private LocalDateTime createdAt;

    /** 更新时间。 */
    private LocalDateTime updatedAt;
}
