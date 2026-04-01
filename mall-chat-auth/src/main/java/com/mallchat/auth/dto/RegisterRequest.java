package com.mallchat.auth.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import lombok.Data;

/** TODO: update docs. */
@Data
public class RegisterRequest {
    /** TODO: update docs. */
    @NotBlank
    @Size(min = 3, max = 32)
    private String username;

    /** TODO: update docs. */
    @NotBlank
    @Size(min = 6, max = 64)
    private String password;

    /** TODO: update docs. */
    @NotBlank
    @Size(min = 2, max = 32)
    private String nickname;
}