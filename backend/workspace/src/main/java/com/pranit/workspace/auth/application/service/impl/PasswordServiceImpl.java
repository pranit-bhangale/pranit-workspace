package com.pranit.workspace.auth.application.service.impl;

import com.pranit.workspace.auth.application.repository.RefreshTokenRepository;
import com.pranit.workspace.auth.application.repository.UserRepository;
import com.pranit.workspace.auth.application.service.PasswordService;
import com.pranit.workspace.auth.domain.dto.ChangeForgotPasswordRequest;
import com.pranit.workspace.auth.domain.dto.ChangePassword;
import com.pranit.workspace.auth.domain.dto.ChangePasswordResponse;
import com.pranit.workspace.auth.domain.dto.ForgotPasswordEmail;
import com.pranit.workspace.auth.domain.dto.PasswordRequest;
import com.pranit.workspace.auth.domain.dto.PasswordResponse;
import com.pranit.workspace.auth.domain.entity.User;
import com.pranit.workspace.auth.exception.PasswordNotMatchingException;
import com.pranit.workspace.auth.exception.PasswordValidationException;
import com.pranit.workspace.auth.exception.UserNotExistsException;
import com.pranit.workspace.notification.api.EmailPurpose;
import com.pranit.workspace.notification.api.OtpService;
import com.pranit.workspace.shared.helper.SecurityContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PasswordServiceImpl implements PasswordService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final OtpService otpService;
    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public PasswordResponse requestPasswordReset(final ForgotPasswordEmail request) {
        final String email = request.email();
        final EmailPurpose purpose = EmailPurpose.PASSWORD_CHANGE;
        final String challengeId = this.userRepository.findByEmail(email)
                .map(user -> {
                    final String id = this.otpService.sendOtp(user.getEmail(), purpose);
                    log.info("Forgot password OTP requested for userId: {}", user.getUserId());
                    return id;
                })
                .orElse(null);

        return PasswordResponse.builder()
                .challengeId(challengeId)
                .message("If the email is registered an OTP has been sent to your email.")
                .build();
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public PasswordResponse resetPassword(final ChangeForgotPasswordRequest request) {
        validatePasswordMatch(request.passwordRequest());
        final String email = request.email().trim().toLowerCase();
        final User user = findUserByEmail(email);
        if (this.passwordEncoder.matches(request.passwordRequest().password(), user.getPassword())) {
            throw new PasswordValidationException("New password cannot be same as old password");
        }
        user.setPassword(this.passwordEncoder.encode(request.passwordRequest().password()));
        invalidateUserSession(user.getUserId());
        log.info("Forgot password reset completed successfully for userId: {}", user.getUserId());
        return PasswordResponse.builder()
                .message("Password changed successfully")
                .build();
    }

    private void validatePasswordMatch(final PasswordRequest request) {
        if (!request.password().equals(request.confirmPassword())) {
            throw new PasswordNotMatchingException("Password and confirm password do not match");
        }
    }

    private User findUserByEmail(final String email) {
        return this.userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("User with email {} not found", email);
                    return new UserNotExistsException("User not found");
                });
    }

    private void invalidateUserSession(final UUID userId) {
        this.refreshTokenRepository.revokeAllByUserId(userId);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public ChangePasswordResponse changeAccountPassword(final ChangePassword request) {
        final UUID userId = SecurityContext.getCurrentUserId();
        final User user = this.userRepository.findByUserId(userId)
                .orElseThrow(() -> {
                    log.warn("User not found for userId: {}", userId);
                    return new UserNotExistsException("User not found");
                });
        validateOldPassword(request.oldPassword(), user.getPassword());
        validatePasswordMatch(request.request());
        user.setPassword(this.passwordEncoder.encode(request.request().confirmPassword()));
        invalidateUserSession(user.getUserId());
        return ChangePasswordResponse.builder()
                .message("Password changed successfully. Please Login  again.")
                .build();
    }

    private void validateOldPassword(final String requestPassword, final String password) {
        if (!this.passwordEncoder.matches(requestPassword, password)) {
            throw new BadCredentialsException("Old password is incorrect");
        }
    }
}
