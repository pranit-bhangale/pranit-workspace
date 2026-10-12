package com.pranit.workspace.auth.domain.dto;

public record UserUpdateRequest(
        String username,
        String fullName,
        String headline,
        UpdateAboutSection about,
        UpdateContactRequest contactInfo
) {

    public record UpdateAboutSection(
            String title,
            String text
    ) {
    }

    public record UpdateContactRequest(
            String email,
            String phone,
            String whatsappNumber
    ) {
    }
}
