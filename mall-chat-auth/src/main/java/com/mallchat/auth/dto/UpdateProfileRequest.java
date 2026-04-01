package com.mallchat.auth.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import lombok.Data;

/** TODO: update docs. */
@Data
public class UpdateProfileRequest {
    @NotBlank(message = "昵称不能为空")
    @Size(min = 2, max = 32, message = "昵称长度需在 2-32 字符")
    @Pattern(regexp = "^[A-Za-z0-9_\\u4e00-\\u9fa5]+$", message = "昵称仅支持中文/字母/数字/下划线")
    private String nickname;

    @Size(max = 255, message = "头像地址过长")
    private String avatarUrl;
}
