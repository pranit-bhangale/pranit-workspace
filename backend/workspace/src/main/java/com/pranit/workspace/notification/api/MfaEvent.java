package com.pranit.workspace.notification.api;

import com.pranit.workspace.notification.mail.event.EmailEvent;
import lombok.Builder;

import java.time.Duration;

@Builder
public record MfaEvent(
        String eventId,
        String email,
        String username,
        String ott,
        Duration expiresAt
) implements EmailEvent {
    public MfaEvent {
        if (expiresAt == null) expiresAt = Duration.ofMinutes(5);
    }
}
