package com.pranit.workspace.auth.domain.dto;

import com.pranit.workspace.auth.domain.entity.User;
import com.pranit.workspace.auth.domain.enums.AccountStatus;
import lombok.Builder;

import java.util.UUID;

@Builder
public record UserResponse(
        UUID userId,
        String username,
        String fullName,
        String email,
        AccountStatus status,
        boolean enabled
) {
    public static UserResponse from(final User user) {
        final String fullName = user.getProfile() != null ? user.getProfile().getFullName() : null;
        return UserResponse.builder()
                .userId(user.getUserId())
                .username(user.getUsername())
                .fullName(fullName)
                .email(user.getEmail())
                .status(user.getStatus())
                .enabled(user.isEnabled())
                .build();
    }
}
