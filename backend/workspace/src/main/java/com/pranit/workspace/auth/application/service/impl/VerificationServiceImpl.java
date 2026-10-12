package com.pranit.workspace.auth.application.service.impl;

import com.pranit.workspace.auth.application.repository.UserRepository;
import com.pranit.workspace.auth.application.service.VerificationService;
import com.pranit.workspace.auth.domain.dto.VerificationResponse;
import com.pranit.workspace.auth.domain.dto.VerifyOtp;
import com.pranit.workspace.auth.domain.entity.User;
import com.pranit.workspace.auth.exception.EmailNotFoundException;
import com.pranit.workspace.notification.api.EmailPurpose;
import com.pranit.workspace.notification.api.OtpService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class VerificationServiceImpl implements VerificationService {

    private final OtpService otpService;
    private final UserRepository userRepository;

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public VerificationResponse verifyRegistration(final VerifyOtp request) {
        final EmailPurpose purpose = EmailPurpose.REGISTRATION;
        final String email = verifyOtp(request.challengeId(), request.otp(), purpose);
        final User user = findUserByEmail(email);
        activateAccount(user);
        return VerificationResponse.builder()
                .message("Account verified successfully.")
                .build();
    }

    private String verifyOtp(String challengeId, String otp, EmailPurpose purpose) {
        return this.otpService.verifyOtp(challengeId, otp, purpose);
    }

    private User findUserByEmail(final String email) {
        return this.userRepository.findByEmail(email)
                .orElseThrow(() -> new EmailNotFoundException("User's email not found"));
    }

    private void activateAccount(final User user) {
        if (!user.isEnabled()) {
            user.setEnabled(true);
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public VerificationResponse verifyPasswordResetOtp(final VerifyOtp request) {
        final EmailPurpose type = EmailPurpose.PASSWORD_CHANGE;
        final String email = verifyOtp(request.challengeId(), request.otp(), type);
        log.warn("Otp successfully verified for email: {}", email);
        return VerificationResponse.builder()
                .message("OTP verified successfully")
                .build();
    }
}
