package com.mallchat.vo.sign;

import java.util.List;
import lombok.Data;

/**
 * 月度签到日历视图对象。
 */
@Data
public class SignCalendarVO {
    /** 年。 */
    private int year;
    /** 月（1-12）。 */
    private int month;
    /** 当月天数。 */
    private int daysInMonth;
    /** 已签到日期列表（从 1 开始）。 */
    private List<Integer> signedDays;
}
