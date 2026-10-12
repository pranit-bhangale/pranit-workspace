package com.pranit.workspace.auth.domain.dto;

import com.pranit.workspace.auth.domain.entity.AboutSection;
import com.pranit.workspace.auth.domain.entity.ContactInfo;
import com.pranit.workspace.auth.domain.entity.Profile;
import com.pranit.workspace.auth.domain.entity.User;
import lombok.Builder;

import java.util.UUID;

@Builder
public record UserUpdateResponse(
        UUID userId,
        String username,
        String fullName,
        String headline,
        AboutResponse about,
        ContactResponse contactInfo
) {

    public static UserUpdateResponse from(final User user) {
        final Profile profile = user.getProfile();
        return UserUpdateResponse.builder()
                .userId(user.getUserId())
                .username(user.getUsername())
                .fullName(profile != null ? profile.getFullName() : null)
                .headline(profile != null ? profile.getHeadline() : null)
                .about(profile != null && profile.getAbout() != null
                        ? AboutResponse.from(profile.getAbout())
                        : null)
                .contactInfo(profile != null && profile.getContactInfo() != null
                        ? ContactResponse.from(profile.getContactInfo())
                        : null)
                .build();
    }

    @Builder
    public record AboutResponse(
            String title,
            String text
    ) {
        public static AboutResponse from(final AboutSection about) {
            return AboutResponse.builder()
                    .title(about.getTitle())
                    .text(about.getText())
                    .build();
        }
    }

    @Builder
    public record ContactResponse(
            String email,
            String phone,
            String whatsappNumber
    ) {
        public static ContactResponse from(final ContactInfo contact) {
            return ContactResponse.builder()
                    .email(contact.getEmail())
                    .phone(contact.getPhone())
                    .whatsappNumber(contact.getWhatsappNumber())
                    .build();
        }
    }
}