package com.pranit.workspace.auth.domain.dto;

import com.pranit.workspace.auth.domain.entity.ProfileMedia;
import lombok.Builder;

@Builder
public record ProfileMediaResponse(
        String avatarKey,
        String resumeKey
) {

    public static ProfileMediaResponse from(final ProfileMedia media) {
        return ProfileMediaResponse.builder()
                .avatarKey(media.getAvatarKey())
                .resumeKey(media.getResumeKey())
                .build();
    }
}
