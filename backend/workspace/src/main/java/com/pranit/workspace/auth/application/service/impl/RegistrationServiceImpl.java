package com.pranit.workspace.auth.application.service.impl;

import com.pranit.workspace.auth.application.repository.UserRepository;
import com.pranit.workspace.auth.application.service.RegistrationService;
import com.pranit.workspace.auth.domain.dto.SignupRequest;
import com.pranit.workspace.auth.domain.dto.SignupResponse;
import com.pranit.workspace.auth.domain.entity.Profile;
import com.pranit.workspace.auth.domain.entity.Role;
import com.pranit.workspace.auth.domain.entity.User;
import com.pranit.workspace.auth.domain.enums.AccountStatus;
import com.pranit.workspace.auth.exception.EmailAlreadyExistsException;
import com.pranit.workspace.auth.exception.PasswordNotMatchingException;
import com.pranit.workspace.auth.exception.UsernameAlreadyExistsException;
import com.pranit.workspace.notification.api.EmailPurpose;
import com.pranit.workspace.notification.api.OtpService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class RegistrationServiceImpl implements RegistrationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final OtpService otpService;

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public SignupResponse createNewAdminAccount(final SignupRequest request) {
        return registerAccount(request);
    }

    private SignupResponse registerAccount(final SignupRequest request) {
        validateUniqueUsernameAndEmail(request.username(), request.email());
        validatePasswordMatch(request.password(), request.confirmPassword());
        final EmailPurpose type = EmailPurpose.REGISTRATION;
        final User user = buildUser(request);
        final User saved = this.userRepository.save(user);
        log.info("New account created successfully for username: {}", saved.getUsername());
        final String challengeId = sendOtp(saved, type);
        return SignupResponse.builder()
                .challengeId(challengeId)
                .message("Check email to verify account and activate it.")
                .build();
    }

    private void validateUniqueUsernameAndEmail(final String username, final String email) {
        if (this.userRepository.existsByUsername(username)) {
            throw new UsernameAlreadyExistsException("Username already taken");
        }
        if (this.userRepository.existsByEmail(email)) {
            throw new EmailAlreadyExistsException("Email already in use.");
        }
    }

    private void validatePasswordMatch(final String password, final String confirmPassword) {
        if (!password.equals(confirmPassword)) {
            throw new PasswordNotMatchingException("Password and confirm password do not match");
        }
    }

    private User buildUser(final SignupRequest request) {
        final Profile profile = Profile.builder()
                .fullName(request.fullName())
                .build();
        final User user = User.builder()
                .username(request.username())
                .email(request.email())
                .password(this.passwordEncoder.encode(request.password()))
                .enabled(false)
                .roles(new HashSet<>(Set.of(Role.ADMIN, Role.EDITOR, Role.USER)))
                .status(AccountStatus.ACTIVE)
                .profile(profile)
                .build();
        profile.setUser(user);
        return user;
    }

    private String sendOtp(final User saved, final EmailPurpose purpose) {
        return this.otpService.sendOtp(saved.getEmail(), purpose);
    }
}
