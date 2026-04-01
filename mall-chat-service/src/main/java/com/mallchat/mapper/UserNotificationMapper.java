package com.mallchat.mapper;

import com.mallchat.model.UserNotification;
import com.mallchat.vo.NotificationItem;
import java.util.List;
import org.apache.ibatis.annotations.Param;

/**
 * User notification mapper.
 */
public interface UserNotificationMapper {
    int insert(UserNotification notification);

    List<NotificationItem> listByUserId(@Param("userId") Long userId,
                                        @Param("readStatus") Integer readStatus,
                                        @Param("offset") int offset,
                                        @Param("limit") int limit);

    Long countByUserId(@Param("userId") Long userId, @Param("readStatus") Integer readStatus);

    int markReadByIds(@Param("userId") Long userId, @Param("ids") List<Long> ids);

    int markAllRead(@Param("userId") Long userId);
}
