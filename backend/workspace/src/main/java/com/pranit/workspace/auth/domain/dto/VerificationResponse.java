package com.pranit.workspace.auth.domain.dto;

import lombok.Builder;

@Builder
public record VerificationResponse(
        String message
) {
}
