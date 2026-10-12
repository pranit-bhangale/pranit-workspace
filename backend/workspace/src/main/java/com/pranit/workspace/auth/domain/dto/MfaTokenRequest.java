package com.pranit.workspace.auth.domain.dto;

import jakarta.validation.constraints.NotBlank;

public record MfaTokenRequest(
        @NotBlank(message = "One time token is required!!!")
        String mfaToken
) {
}
