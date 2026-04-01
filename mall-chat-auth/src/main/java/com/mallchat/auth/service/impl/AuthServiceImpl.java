package com.mallchat.auth.service.impl;

import com.mallchat.auth.dto.LoginRequest;
import com.mallchat.auth.client.SignServiceClient;
import com.mallchat.auth.dto.RegisterRequest;
import com.mallchat.auth.dto.SmsLoginRequest;
import com.mallchat.auth.dto.UpdateProfileRequest;
import com.mallchat.auth.exception.BusinessException;
import com.mallchat.auth.mapper.UserAccountMapper;
import com.mallchat.auth.model.UserAccount;
import com.mallchat.auth.service.AuthService;
import com.mallchat.auth.util.JwtUtil;
import com.mallchat.auth.util.PasswordUtil;
import com.mallchat.auth.vo.LoginResponse;
import com.mallchat.auth.vo.ProfileResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

/** 认证服务实现。 */
@Service
public class AuthServiceImpl implements AuthService {
    private static final String SMS_CODE_PREFIX = "verify:sms:";

    private final UserAccountMapper userAccountMapper;
    private final StringRedisTemplate redisTemplate;
    private final SignServiceClient signServiceClient;

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Value("${app.jwt.expire-minutes}")
    private int expireMinutes;

    public AuthServiceImpl(UserAccountMapper userAccountMapper,
                           StringRedisTemplate redisTemplate,
                           SignServiceClient signServiceClient) {
        this.userAccountMapper = userAccountMapper;
        this.redisTemplate = redisTemplate;
        this.signServiceClient = signServiceClient;
    }

    /** 注册。 */
    @Override
    public boolean register(RegisterRequest request) {
        UserAccount exists = userAccountMapper.findByUsername(request.getUsername());
        if (exists != null) {
            return false;
        }
        UserAccount user = new UserAccount();
        user.setUsername(request.getUsername());
        user.setPasswordHash(PasswordUtil.hash(request.getPassword()));
        user.setNickname(request.getNickname());
        user.setStatus(1);
        return userAccountMapper.insert(user) > 0;
    }

    /** 账号密码登录。 */
    @Override
    public LoginResponse login(LoginRequest request) {
        UserAccount user = userAccountMapper.findByUsername(request.getUsername());
        if (user == null || user.getStatus() != 1) {
            return null;
        }
        if (!PasswordUtil.matches(request.getPassword(), user.getPasswordHash())) {
            return null;
        }
        String token = JwtUtil.generateToken(user.getId(), user.getUsername(), jwtSecret, expireMinutes);
        autoCheckIn(token);
        LoginResponse response = new LoginResponse();
        response.setUserId(user.getId());
        response.setUsername(user.getUsername());
        response.setNickname(user.getNickname());
        response.setToken(token);
        return response;
    }

    /** 短信验证码登录。 */
    @Override
    public LoginResponse loginBySms(SmsLoginRequest request) {
        String phone = request.getPhone() == null ? "" : request.getPhone().trim();
        String code = request.getCode() == null ? "" : request.getCode().trim();
        if (phone.isEmpty() || code.isEmpty()) {
            throw new BusinessException("手机号或验证码不能为空");
        }
        String key = SMS_CODE_PREFIX + phone;
        String cached = redisTemplate.opsForValue().get(key);
        if (cached == null || !cached.equals(code)) {
            throw new BusinessException("验证码错误或已过期");
        }
        UserAccount user = userAccountMapper.findByUsername(phone);
        if (user == null || user.getStatus() == null || user.getStatus() != 1) {
            throw new BusinessException("手机号未注册或账号不可用");
        }
        redisTemplate.delete(key);
        String token = JwtUtil.generateToken(user.getId(), user.getUsername(), jwtSecret, expireMinutes);
        autoCheckIn(token);
        LoginResponse response = new LoginResponse();
        response.setUserId(user.getId());
        response.setUsername(user.getUsername());
        response.setNickname(user.getNickname());
        response.setToken(token);
        return response;
    }

    private void autoCheckIn(String token) {
        try {
            signServiceClient.checkIn("Bearer " + token);
        } catch (Exception ex) {
            // ignore auto check-in failures
        }
    }

    /** 获取个人资料。 */
    @Override
    public ProfileResponse getProfile(Long userId) {
        UserAccount user = userAccountMapper.findById(userId);
        if (user == null || user.getStatus() == null || user.getStatus() != 1) {
            return null;
        }
        return toProfile(user);
    }

    /** 更新个人资料。 */
    @Override
    public ProfileResponse updateProfile(Long userId, UpdateProfileRequest request) {
        UserAccount user = userAccountMapper.findById(userId);
        if (user == null || user.getStatus() == null || user.getStatus() != 1) {
            return null;
        }
        String nickname = request.getNickname() == null ? "" : request.getNickname().trim();
        String avatarUrl = request.getAvatarUrl() == null ? null : request.getAvatarUrl().trim();
        if (avatarUrl != null && avatarUrl.isEmpty()) {
            avatarUrl = null;
        }
        userAccountMapper.updateProfile(userId, nickname, avatarUrl);
        user.setNickname(nickname);
        user.setAvatarUrl(avatarUrl);
        return toProfile(user);
    }

    private ProfileResponse toProfile(UserAccount user) {
        ProfileResponse response = new ProfileResponse();
        response.setUserId(user.getId());
        response.setUsername(user.getUsername());
        response.setNickname(user.getNickname());
        response.setAvatarUrl(user.getAvatarUrl());
        return response;
    }
}
