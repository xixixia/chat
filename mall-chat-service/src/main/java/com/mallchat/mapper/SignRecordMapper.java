package com.mallchat.mapper;

import com.mallchat.model.SignRecord;

/**
 * 签到记录 Mapper。
 */
public interface SignRecordMapper {
    /**
     * 插入签到记录（若已存在则忽略）。
     *
     * @param record 签到记录
     * @return 影响行数
     */
    int insertIgnore(SignRecord record);
}
