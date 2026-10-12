package com.pranit.workspace.notification.mail.email.handler;

import com.pranit.workspace.notification.api.EmailPurpose;
import com.pranit.workspace.notification.api.OtpEvent;
import com.pranit.workspace.notification.mail.email.EmailHandler;
import com.pranit.workspace.notification.mail.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public final class RegistrationOtpHandler implements EmailHandler<OtpEvent> {

    private final EmailService emailService;

    @Override
    public EmailPurpose supports() {
        return EmailPurpose.REGISTRATION;
    }

    @Override
    public void send(final OtpEvent event) {
        log.info("Sending registration OTP. EventID: {}", event.eventId());
        emailService.sendEmail(event.email(),
                "Account Activation OTP",
                "registration-otp",
                Map.of("otp", event.otp(),
                        "expiresInMinutes", event.expiresAt().toMinutes()));
    }
}