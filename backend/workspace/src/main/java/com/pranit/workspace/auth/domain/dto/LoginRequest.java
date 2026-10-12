package com.pranit.workspace.auth.domain.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record LoginRequest(
        @NotBlank(message = "Username is mandatory")
        String username,
        @NotBlank(message = "Password is mandatory")
        String password
) {
}
