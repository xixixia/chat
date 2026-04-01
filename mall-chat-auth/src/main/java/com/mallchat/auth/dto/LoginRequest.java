package com.mallchat.auth.dto;

import javax.validation.constraints.NotBlank;
import lombok.Data;

/** TODO: update docs. */
@Data
public class LoginRequest {
    /** TODO: update docs. */
    @NotBlank
    private String username;

    /** TODO: update docs. */
    @NotBlank
    private String password;
}