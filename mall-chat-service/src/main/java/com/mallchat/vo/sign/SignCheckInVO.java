package com.mallchat.vo.sign;

import lombok.Data;

/**
 * 签到返回视图对象。
 */
@Data
public class SignCheckInVO {
    /** 调用后是否已签到。 */
    private boolean signedToday;
    /** 是否为当天首次签到。 */
    private boolean firstSign;
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
    /** 本次签到目标日期（yyyy-MM-dd）。 */
    private String targetDate;
    /** 是否补签。 */
    private boolean retro;
}
