package com.mallchat.controller;

import com.mallchat.common.Result;
import com.mallchat.exception.BusinessException;
import com.mallchat.exception.UnauthorizedException;
import com.mallchat.security.UserContext;
import com.mallchat.service.SignService;
import com.mallchat.vo.sign.SignCalendarVO;
import com.mallchat.vo.sign.SignCheckInVO;
import com.mallchat.vo.sign.SignStatusVO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 当前用户签到相关接口。
 */
@RestController
@RequestMapping("/sign")
public class SignController {
    private final SignService signService;

    public SignController(SignService signService) {
        this.signService = signService;
    }

    /**
     * 今日签到。
     *
     * @return 签到结果
     */
    @PostMapping("/check-in")
    public Result<SignCheckInVO> checkIn() {
        Long userId = requireUserId();
        return Result.ok(signService.checkIn(userId));
    }

    /**
     * 补签指定日期。
     *
     * @param date 日期（yyyy-MM-dd）
     * @return 签到结果
     */
    @PostMapping("/retro")
    public Result<SignCheckInVO> retro(@RequestParam String date) {
        Long userId = requireUserId();
        return Result.ok(signService.retroCheckIn(userId, date));
    }

    /**
     * 获取今日签到状态与连续天数。
     *
     * @return 签到状态
     */
    @GetMapping("/status")
    public Result<SignStatusVO> status() {
        Long userId = requireUserId();
        return Result.ok(signService.getStatus(userId));
    }

    /**
     * 获取指定月份已签到日期列表。
     *
     * @param year  年
     * @param month 月（1-12）
     * @return 月历结果
     */
    @GetMapping("/calendar")
    public Result<SignCalendarVO> calendar(@RequestParam int year, @RequestParam int month) {
        Long userId = requireUserId();
        return Result.ok(signService.getCalendar(userId, year, month));
    }

    /**
     * 奖励发放预留接口（暂未实现）。
     *
     * @return 未实现错误
     */
    @PostMapping("/reward/dispatch")
    public Result<Void> dispatchReward() {
        throw new BusinessException("奖励发放暂未实现");
    }

    /**
     * 校验当前用户 ID 是否存在。
     *
     * @return 用户 ID
     */
    private Long requireUserId() {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw new UnauthorizedException("未登录");
        }
        return userId;
    }
}
