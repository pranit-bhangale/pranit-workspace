package com.pranit.workspace.portfolio.domain.dto;

import com.pranit.workspace.portfolio.domain.enums.MessageStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record ContactStatusUpdateRequest(
        @NotNull(message = "Message status is required")
        MessageStatus status
) {
}
