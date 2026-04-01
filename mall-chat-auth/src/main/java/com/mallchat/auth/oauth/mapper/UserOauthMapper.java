package com.mallchat.auth.oauth.mapper;

import com.mallchat.auth.oauth.model.UserOauth;
import org.apache.ibatis.annotations.Param;

/** TODO: update docs. */
public interface UserOauthMapper {
    /** TODO: update docs. */
    UserOauth findByProviderAndOpenId(@Param("provider") String provider, @Param("openId") String openId);

    /** TODO: update docs. */
    int insert(UserOauth userOauth);
}