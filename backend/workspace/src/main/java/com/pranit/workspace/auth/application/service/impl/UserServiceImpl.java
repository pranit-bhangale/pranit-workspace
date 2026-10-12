package com.pranit.workspace.auth.application.service.impl;

import com.pranit.workspace.auth.application.repository.UserRepository;
import com.pranit.workspace.auth.application.service.UserService;
import com.pranit.workspace.auth.domain.dto.ProfileMediaResponse;
import com.pranit.workspace.auth.domain.dto.UserUpdateRequest;
import com.pranit.workspace.auth.domain.dto.UserUpdateResponse;
import com.pranit.workspace.auth.domain.entity.AboutSection;
import com.pranit.workspace.auth.domain.entity.ContactInfo;
import com.pranit.workspace.auth.domain.entity.Profile;
import com.pranit.workspace.auth.domain.entity.ProfileMedia;
import com.pranit.workspace.auth.domain.entity.User;
import com.pranit.workspace.auth.security.helper.SecurityContext;
import com.pranit.workspace.minio.api.MinioService;
import com.pranit.workspace.shared.exception.InvalidRequestException;
import com.pranit.workspace.shared.exception.ResourceNotFoundException;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private static final Set<String> ALLOWED_AVATAR_EXTENSIONS = Set.of("jpg", "jpeg", "png");
    private static final Set<String> ALLOWED_RESUME_EXTENSIONS = Set.of("pdf");
    private static final long MAX_AVATAR_SIZE = 5 * 1024 * 1024;
    private static final long MAX_RESUME_SIZE = 10 * 1024 * 1024;
    private final TransactionTemplate transactionTemplate;
    private final UserRepository userRepository;
    private final MinioService storageService;

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

    @Override
    public ProfileMediaResponse uploadProfileMedia(final MultipartFile avatar, final MultipartFile resume) {
        if ((avatar == null || avatar.isEmpty()) && (resume == null || resume.isEmpty())) {
            throw new InvalidRequestException("At least one file must be provided");
        }
        if (avatar != null && !avatar.isEmpty()) {
            validateFile(avatar, ALLOWED_AVATAR_EXTENSIONS, MAX_AVATAR_SIZE, "Avatar");
        }
        if (resume != null && !resume.isEmpty()) {
            validateFile(resume, ALLOWED_RESUME_EXTENSIONS, MAX_RESUME_SIZE, "Resume");
        }
        final UUID currentUserId = SecurityContext.getCurrentUserId();
        String newAvatarKey = null;
        String newResumeKey = null;
        try {
            if (avatar != null && !avatar.isEmpty()) newAvatarKey = uploadToMinio("avatars", avatar);
            if (resume != null && !resume.isEmpty()) newResumeKey = uploadToMinio("resumes", resume);
            final String finalAvatarKey = newAvatarKey;
            final String finalResumeKey = newResumeKey;
            final ProfileMediaUpdateResult result = this.transactionTemplate.execute(_ ->
                    saveMediaKeysToDatabase(currentUserId, finalAvatarKey, finalResumeKey)
            );
            if (result != null && result.oldKeysToDelete() != null) {
                for (String oldKey : result.oldKeysToDelete()) {
                    deleteSafely(oldKey);
                }
                return result.response();
            }
            return ProfileMediaResponse.builder().build();
        } catch (Exception e) {
            deleteSafely(newAvatarKey);
            deleteSafely(newResumeKey);
            log.error("Failed to upload profile media for user {}", currentUserId, e);
            if (e instanceof InvalidRequestException || e instanceof ResourceNotFoundException) {
                throw (RuntimeException) e;
            }
            throw new IllegalStateException("Failed to upload profile media", e);
        }
    }

    @Transactional(readOnly = true)
    public ProfileMediaResponse getProfileMedia() {
        final UUID currentUserId = SecurityContext.getCurrentUserId();
        final User user = this.userRepository.findByUserIdWithProfile(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "userId", currentUserId));
        if (user.getProfile() == null || user.getProfile().getMedia() == null) {
            return new ProfileMediaResponse(null, null);
        }
        return ProfileMediaResponse.from(user.getProfile().getMedia());
    }

    private void validateFile
            (final MultipartFile file, final Set<String> allowedExtensions, final long maxSize, final String fileType) {
        if (file.isEmpty()) throw new InvalidRequestException(fileType + " must not be empty");
        if (file.getSize() > maxSize) throw new InvalidRequestException(fileType + " exceeds maximum allowed size");
        final String extension = StringUtils.getFilenameExtension(file.getOriginalFilename());
        if (!StringUtils.hasText(extension) || !allowedExtensions.contains(extension.toLowerCase(Locale.ROOT))) {
            throw new InvalidRequestException("Invalid " + fileType + " extension");
        }
        try {
            validateFileSignature(file, extension.toLowerCase(Locale.ROOT), fileType);
        } catch (IOException e) {
            throw new InvalidRequestException("Unable to read " + fileType + " file");
        }
    }

    private String uploadToMinio(final String folder, final MultipartFile file) throws Exception {
        final String extension = Objects.requireNonNull(
                StringUtils.getFilenameExtension(file.getOriginalFilename())).toLowerCase(Locale.ROOT);
        final String objectName = folder + "/" + UUID.randomUUID() + "." + extension;
        final String contentType = switch (extension) {
            case "jpg", "jpeg" -> "image/jpeg";
            case "png" -> "image/png";
            case "pdf" -> "application/pdf";
            default -> throw new InvalidRequestException("Unsupported file extension");
        };
        try (var input = file.getInputStream()) {
            this.storageService.upload(objectName, input, file.getSize(), contentType);
        }
        return objectName;
    }

    public ProfileMediaUpdateResult saveMediaKeysToDatabase
            (final UUID userId, final String newAvatarKey, final String newResumeKey) {
        final User user = this.userRepository.findByUserIdWithProfile(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "userId", userId));
        Profile profile = user.getProfile();
        if (profile == null) {
            profile = Profile.builder().build();
            user.setProfile(profile);
        }
        ProfileMedia media = profile.getMedia();
        if (media == null) {
            media = ProfileMedia.builder().build();
            profile.setMedia(media);
        }
        final List<String> oldKeysToDelete = new ArrayList<>();
        if (newAvatarKey != null) {
            String oldKey = media.getAvatarKey();
            if (StringUtils.hasText(oldKey) && !oldKey.equals(newAvatarKey)) {
                oldKeysToDelete.add(oldKey);
            }
            media.setAvatarKey(newAvatarKey);
        }
        if (newResumeKey != null) {
            String oldKey = media.getResumeKey();
            if (StringUtils.hasText(oldKey) && !oldKey.equals(newResumeKey)) {
                oldKeysToDelete.add(oldKey);
            }
            media.setResumeKey(newResumeKey);
        }
        this.userRepository.save(user);
        return new ProfileMediaUpdateResult(ProfileMediaResponse.from(media), oldKeysToDelete);
    }

    private void deleteSafely(final String objectName) {
        if (!StringUtils.hasText(objectName)) return;
        try {
            this.storageService.delete(objectName);
        } catch (Exception e) {
            log.warn("Failed to delete MinIO object {}", objectName, e);
        }
    }

    private void validateFileSignature(final MultipartFile file, final String extension, final String fileType) throws IOException {
        final byte[] header;
        try (InputStream input = file.getInputStream()) {
            header = input.readNBytes(8);
        }
        final boolean valid = switch (extension) {
            case "jpg", "jpeg" ->
                    header.length >= 3 && (header[0] & 0xFF) == 0xFF && (header[1] & 0xFF) == 0xD8 && (header[2] & 0xFF) == 0xFF;
            case "png" ->
                    header.length >= 8 && (header[0] & 0xFF) == 0x89 && header[1] == 0x50 && header[2] == 0x4E && header[3] == 0x47;
            case "pdf" ->
                    header.length >= 5 && header[0] == 0x25 && header[1] == 0x50 && header[2] == 0x44 && header[3] == 0x46 && header[4] == 0x2D;
            default -> false;
        };
        if (!valid) throw new InvalidRequestException("Invalid " + fileType + " file content signature");
    }

    @Builder
    public record ProfileMediaUpdateResult(ProfileMediaResponse response, List<String> oldKeysToDelete) {
    }
}
