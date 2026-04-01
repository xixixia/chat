package com.mallchat.mapper;

import com.mallchat.model.UserAccount;
import org.apache.ibatis.annotations.Param;

/** TODO: update docs. */
public interface UserAccountMapper {
    /** TODO: update docs. */
    UserAccount findByUsername(@Param("username") String username);

    /** TODO: update docs. */
    int insert(UserAccount userAccount);
}