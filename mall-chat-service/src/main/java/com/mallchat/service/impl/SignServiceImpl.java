package com.mallchat.service.impl;

import com.mallchat.exception.BusinessException;
import com.mallchat.mapper.SignRecordMapper;
import com.mallchat.model.SignRecord;
import com.mallchat.service.SignService;
import com.mallchat.vo.sign.SignCalendarVO;
import com.mallchat.vo.sign.SignCheckInVO;
import com.mallchat.vo.sign.SignStatusVO;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

/**
 * 基于 Redis Bitmap 的签到实现。
 *
 * <p>月维度 key：sign:month:{userId}:{yyyyMM}，offset = dayOfMonth - 1。</p>
 * <p>年维度 key：sign:year:{userId}:{yyyy}，offset = dayOfYear - 1。</p>
 */
@Service
public class SignServiceImpl implements SignService {
    private static final ZoneId ZONE = ZoneId.of("Asia/Shanghai");
    private static final String MONTH_PREFIX = "sign:month:";
    private static final String YEAR_PREFIX = "sign:year:";
    private static final DateTimeFormatter MONTH_FORMAT = DateTimeFormatter.ofPattern("yyyyMM");
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final StringRedisTemplate redisTemplate;
    private final SignRecordMapper signRecordMapper;

    public SignServiceImpl(StringRedisTemplate redisTemplate, SignRecordMapper signRecordMapper) {
        this.redisTemplate = redisTemplate;
        this.signRecordMapper = signRecordMapper;
    }

    /**
     * 今日签到：使用 SETBIT 写入，并通过反向扫描计算连续天数。
     */
    @Override
    public SignCheckInVO checkIn(Long userId) {
        LocalDate today = LocalDate.now(ZONE);
        return doCheckIn(userId, today, false);
    }

    /**
     * 补签指定日期。
     */
    @Override
    public SignCheckInVO retroCheckIn(Long userId, String date) {
        LocalDate target;
        try {
            target = LocalDate.parse(date, DATE_FORMAT);
        } catch (Exception ex) {
            throw new BusinessException("日期格式错误，应为 yyyy-MM-dd");
        }
        LocalDate today = LocalDate.now(ZONE);
        if (target.isAfter(today)) {
            throw new BusinessException("不允许补签未来日期");
        }
        return doCheckIn(userId, target, true);
    }

    /**
     * 获取今日签到状态与连续天数。
     */
    @Override
    public SignStatusVO getStatus(Long userId) {
        LocalDate today = LocalDate.now(ZONE);
        String monthKey = monthKey(userId, YearMonth.from(today));
        boolean signedToday = getBit(monthKey, today.getDayOfMonth() - 1);
        LocalDate streakBase = signedToday ? today : today.minusDays(1);
        int streak = computeStreak(userId, streakBase);
        long total = bitCount(yearKey(userId, today.getYear()));

        SignStatusVO vo = new SignStatusVO();
        vo.setSignedToday(signedToday);
        vo.setStreak(streak);
        vo.setTotal(total);
        vo.setYear(today.getYear());
        vo.setMonth(today.getMonthValue());
        vo.setDay(today.getDayOfMonth());
        return vo;
    }

    private SignCheckInVO doCheckIn(Long userId, LocalDate date, boolean retro) {
        int dayOffset = date.getDayOfMonth() - 1;
        String monthKey = monthKey(userId, YearMonth.from(date));
        String yearKey = yearKey(userId, date.getYear());

        Boolean existed = redisTemplate.opsForValue().setBit(monthKey, dayOffset, true);
        redisTemplate.opsForValue().setBit(yearKey, date.getDayOfYear() - 1, true);

        boolean alreadySigned = Boolean.TRUE.equals(existed);
        if (!alreadySigned) {
            insertRecord(userId, date, retro);
        }
        SignStatusVO status = getStatus(userId);

        SignCheckInVO vo = new SignCheckInVO();
        vo.setSignedToday(status.isSignedToday());
        vo.setFirstSign(!alreadySigned);
        vo.setStreak(status.getStreak());
        vo.setTotal(bitCount(yearKey));
        vo.setYear(date.getYear());
        vo.setMonth(date.getMonthValue());
        vo.setDay(date.getDayOfMonth());
        vo.setTargetDate(date.format(DATE_FORMAT));
        vo.setRetro(retro);
        return vo;
    }

    /**
     * 获取指定月份已签到的日期列表。
     */
    @Override
    public SignCalendarVO getCalendar(Long userId, int year, int month) {
        YearMonth yearMonth = YearMonth.of(year, month);
        String monthKey = monthKey(userId, yearMonth);
        int days = yearMonth.lengthOfMonth();
        List<Integer> signedDays = new ArrayList<>();
        for (int i = 1; i <= days; i++) {
            if (getBit(monthKey, i - 1)) {
                signedDays.add(i);
            }
        }
        SignCalendarVO vo = new SignCalendarVO();
        vo.setYear(year);
        vo.setMonth(month);
        vo.setDaysInMonth(days);
        vo.setSignedDays(signedDays);
        return vo;
    }

    /**
     * 反向扫描 Bitmap 计算连续签到天数。
     */
    private int computeStreak(Long userId, LocalDate date) {
        if (date == null) {
            return 0;
        }
        int streak = 0;
        LocalDate cursor = date;
        while (true) {
            YearMonth yearMonth = YearMonth.from(cursor);
            String key = monthKey(userId, yearMonth);
            int offset = cursor.getDayOfMonth() - 1;
            if (!getBit(key, offset)) {
                break;
            }
            streak++;
            cursor = cursor.minusDays(1);
        }
        return streak;
    }

    /**
     * 读取指定 offset 的 bit。
     */
    private boolean getBit(String key, int offset) {
        Boolean value = redisTemplate.opsForValue().getBit(key, offset);
        return Boolean.TRUE.equals(value);
    }

    /**
     * 统计指定 key 的 bit 数量。
     */
    private long bitCount(String key) {
        return redisTemplate.execute((RedisCallback<Long>) connection -> {
            byte[] rawKey = redisTemplate.getStringSerializer().serialize(key);
            if (rawKey == null) {
                return 0L;
            }
            Long count = connection.bitCount(rawKey);
            return count != null ? count : 0L;
        });
    }

    /**
     * 构建月维度 Bitmap key。
     */
    private String monthKey(Long userId, YearMonth yearMonth) {
        return MONTH_PREFIX + userId + ":" + yearMonth.format(MONTH_FORMAT);
    }

    /**
     * 构建年维度 Bitmap key。
     */
    private String yearKey(Long userId, int year) {
        return YEAR_PREFIX + userId + ":" + year;
    }

    private void insertRecord(Long userId, LocalDate date, boolean retro) {
        SignRecord record = new SignRecord();
        record.setUserId(userId);
        record.setSignDate(date);
        record.setSignTime(LocalDateTime.now(ZONE));
        record.setIsRetro(retro ? 1 : 0);
        record.setRewardStatus(0);
        signRecordMapper.insertIgnore(record);
    }
}
