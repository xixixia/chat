package com.mallchat.auth.mapper;

import com.mallchat.auth.model.UserAccount;
import org.apache.ibatis.annotations.Param;

/** TODO: update docs. */
public interface UserAccountMapper {
    /** TODO: update docs. */
    UserAccount findByUsername(@Param("username") String username);

    /** TODO: update docs. */
    UserAccount findById(@Param("id") Long id);

    /** TODO: update docs. */
    int insert(UserAccount userAccount);

    /** TODO: update docs. */
    int updateProfile(@Param("id") Long id,
                      @Param("nickname") String nickname,
                      @Param("avatarUrl") String avatarUrl);
}
