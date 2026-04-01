package com.mallchat.vo.sign;

import lombok.Data;

/**
 * 今日签到状态视图对象。
 */
@Data
public class SignStatusVO {
    /** 今日是否已签到。 */
    private boolean signedToday;
    /** 连续签到天数。 */
    private int streak;
    /** 当前年度累计签到天数。 */
    private long total;
    /** 当前年。 */
    private int year;
    /** 当前月。 */
    private int month;
    /** 当月日。 */
    private int day;
}
