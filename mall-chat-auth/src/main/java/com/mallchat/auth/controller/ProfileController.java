package com.mallchat.auth.controller;

import com.mallchat.auth.common.Result;
import com.mallchat.auth.dto.CosStsRequest;
import com.mallchat.auth.dto.UpdateProfileRequest;
import com.mallchat.auth.service.AuthService;
import com.mallchat.auth.service.CosStsService;
import com.mallchat.auth.util.JwtUtil;
import com.mallchat.auth.vo.CosStsResponse;
import com.mallchat.auth.vo.CosUploadConfigResponse;
import com.mallchat.auth.vo.ProfileResponse;
import io.jsonwebtoken.Claims;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;

/** TODO: update docs. */
@RestController
@RequestMapping("/auth")
public class ProfileController {
    private final AuthService authService;
    private final CosStsService cosStsService;

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Value("${app.cos.allowed-types:image/jpeg,image/png,image/webp,image/gif}")
    private String allowedTypes;

    @Value("${app.cos.max-size-mb:5}")
    private int maxSizeMb;

    public ProfileController(AuthService authService, CosStsService cosStsService) {
        this.authService = authService;
        this.cosStsService = cosStsService;
    }

    /** TODO: update docs. */
    @GetMapping("/me")
    public Result<ProfileResponse> me(HttpServletRequest request, HttpServletResponse response) {
        Long userId = requireUserId(request, response);
        if (userId == null) {
            return Result.error("未登录");
        }
        ProfileResponse profile = authService.getProfile(userId);
        if (profile == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return Result.error("用户不存在");
        }
        return Result.ok(profile);
    }

    /**
     * 获取 COS 临时凭证（需要登录）。
     */
    @PostMapping("/cos/sts")
    public Result<CosStsResponse> issueCosSts(@Valid @RequestBody CosStsRequest requestBody,
                                              HttpServletRequest request,
                                              HttpServletResponse response) {
        Long userId = requireUserId(request, response);
        if (userId == null) {
            return Result.error("未登录");
        }
        CosStsResponse sts = cosStsService.issueUpload(requestBody.getFilename(), requestBody.getPrefix());
        return Result.ok(sts);
    }

    /**
     * 获取 COS 上传配置（需要登录）。
     */
    @GetMapping("/cos/config")
    public Result<CosUploadConfigResponse> cosConfig(HttpServletRequest request, HttpServletResponse response) {
        Long userId = requireUserId(request, response);
        if (userId == null) {
            return Result.error("未登录");
        }
        List<String> types = Arrays.stream(allowedTypes.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
        CosUploadConfigResponse config = new CosUploadConfigResponse();
        config.setAllowedTypes(types);
        config.setMaxSizeMb(maxSizeMb);
        return Result.ok(config);
    }

    /** TODO: update docs. */
    @PutMapping("/profile")
    public Result<ProfileResponse> update(@Valid @RequestBody UpdateProfileRequest request,
                                          HttpServletRequest httpRequest,
                                          HttpServletResponse response) {
        Long userId = requireUserId(httpRequest, response);
        if (userId == null) {
            return Result.error("未登录");
        }
        ProfileResponse profile = authService.updateProfile(userId, request);
        if (profile == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return Result.error("未登录");
        }
        return Result.ok(profile);
    }

    private Long requireUserId(HttpServletRequest request, HttpServletResponse response) {
        String auth = request.getHeader("Authorization");
        if (auth == null || !auth.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return null;
        }
        String token = auth.substring(7);
        try {
            Claims claims = JwtUtil.parse(token, jwtSecret);
            Object userId = claims.get("userId");
            if (userId == null) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return null;
            }
            return Long.valueOf(String.valueOf(userId));
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return null;
        }
    }
}
