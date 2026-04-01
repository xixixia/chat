package com.mallchat.service;

import com.mallchat.vo.sign.SignCalendarVO;
import com.mallchat.vo.sign.SignCheckInVO;
import com.mallchat.vo.sign.SignStatusVO;

/**
 * 签到服务，负责每日签到与统计查询。
 */
public interface SignService {
    /**
     * 用户每日签到。
     *
     * @param userId 用户 ID
     * @return 签到结果，包含连续天数与累计天数
     */
    SignCheckInVO checkIn(Long userId);

    /**
     * 用户补签指定日期。
     *
     * @param userId 用户 ID
     * @param date   日期（yyyy-MM-dd）
     * @return 签到结果，包含连续天数与累计天数
     */
    SignCheckInVO retroCheckIn(Long userId, String date);

    /**
     * 获取用户今日签到状态与连续天数。
     *
     * @param userId 用户 ID
     * @return 签到状态
     */
    SignStatusVO getStatus(Long userId);

    /**
     * 获取指定月份已签到的日期列表。
     *
     * @param userId 用户 ID
     * @param year   年
     * @param month  月（1-12）
     * @return 月历结果
     */
    SignCalendarVO getCalendar(Long userId, int year, int month);
}
