package com.mallchat.register.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import lombok.Data;

/** TODO: update docs. */
@Data
public class EmailRegisterRequest {
    /** TODO: update docs. */
    @NotBlank
    @Email
    private String email;

    /** TODO: update docs. */
    @NotBlank
    private String code;

    /** TODO: update docs. */
    @NotBlank
    @Size(min = 6, max = 64)
    private String password;

    /** TODO: update docs. */
    @NotBlank
    @Size(min = 2, max = 32)
    private String nickname;
}
