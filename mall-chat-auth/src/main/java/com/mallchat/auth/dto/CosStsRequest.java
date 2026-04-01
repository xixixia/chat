package com.mallchat.auth.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import lombok.Data;

/**
 * COS STS request.
 */
@Data
public class CosStsRequest {
    @NotBlank(message = "filename is required")
    @Size(max = 128, message = "filename too long")
    private String filename;

    @Size(max = 64, message = "prefix too long")
    private String prefix;
}
