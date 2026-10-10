package com.pranit.workspace.auth.domain.dto;

import lombok.Builder;

@Builder
public record ChangePasswordResponse(
        String message
) {
}
