package com.pranit.workspace.auth.domain.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record ChangePassword(
        @NotBlank(message = "Old Password is required!!")
        String oldPassword,
        PasswordRequest request
) {
}
