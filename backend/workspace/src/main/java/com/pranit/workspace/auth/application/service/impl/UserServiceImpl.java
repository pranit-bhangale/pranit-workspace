package com.pranit.workspace.auth.application.service.impl;

import com.pranit.workspace.auth.application.repository.UserRepository;
import com.pranit.workspace.auth.application.service.UserService;
import com.pranit.workspace.auth.domain.dto.UserUpdateRequest;
import com.pranit.workspace.auth.domain.dto.UserUpdateResponse;
import com.pranit.workspace.auth.domain.entity.AboutSection;
import com.pranit.workspace.auth.domain.entity.ContactInfo;
import com.pranit.workspace.auth.domain.entity.Profile;
import com.pranit.workspace.auth.domain.entity.User;
import com.pranit.workspace.auth.security.helper.SecurityContext;
import com.pranit.workspace.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public UserUpdateResponse updateCurrentUser(final UserUpdateRequest request) {
        final UUID currentUserId = SecurityContext.getCurrentUserId();
        log.info("Updating profile details for current user: {}", currentUserId);
        final User user = this.userRepository.findByUserIdWithProfile(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "userId", currentUserId));
        final Profile profile = user.getProfile();
        if (request.username() != null) user.setUsername(request.username());
        if (profile != null) {
            if (request.fullName() != null) profile.setFullName(request.fullName());
            if (request.headline() != null) profile.setHeadline(request.headline());
            if (request.about() != null) {
                final AboutSection about = profile.getAbout();
                if (about != null) {
                    if (request.about().title() != null) about.setTitle(request.about().title());
                    if (request.about().text() != null) about.setText(request.about().text());
                }
            }
            if (request.contactInfo() != null) {
                final ContactInfo contact = profile.getContactInfo();
                if (contact != null) {
                    if (request.contactInfo().email() != null) contact.setEmail(request.contactInfo().email());
                    if (request.contactInfo().phone() != null) contact.setPhone(request.contactInfo().phone());
                    if (request.contactInfo().whatsappNumber() != null)
                        contact.setWhatsappNumber(request.contactInfo().whatsappNumber());
                }
            }
        }
        return UserUpdateResponse.from(user);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteCurrentUser() {
        final UUID currentUserId = SecurityContext.getCurrentUserId();
        log.info("Deleting user: {}", currentUserId);
        final User user = this.userRepository.findByUserId(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "userId", currentUserId));
        this.userRepository.delete(user);
    }
}
