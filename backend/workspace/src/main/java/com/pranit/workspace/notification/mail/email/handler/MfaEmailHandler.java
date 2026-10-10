package com.pranit.workspace.notification.mail.email.handler;

import com.pranit.workspace.notification.api.EmailPurpose;
import com.pranit.workspace.notification.api.MfaEvent;
import com.pranit.workspace.notification.mail.email.EmailHandler;
import com.pranit.workspace.notification.mail.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class MfaEmailHandler implements EmailHandler<MfaEvent> {

    private final EmailService emailService;

    @Override
    public EmailPurpose supports() {
        return EmailPurpose.OTT;
    }

    @Override
    public void send(final MfaEvent event) {
        log.info("Sending MFA email. EventID: {}, username: {}", event.eventId(), event.username());
        emailService.sendEmail(event.email(),
                "Complete your sign-in",
                "mfa-email",
                Map.of("username", event.username(),
                        "verificationUrl", event.ott(),
                        "expiresInMinutes", event.expiresAt().toMinutes()));
    }
}