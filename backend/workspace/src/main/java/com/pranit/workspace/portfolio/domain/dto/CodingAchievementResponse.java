package com.pranit.workspace.portfolio.domain.dto;

import com.pranit.workspace.portfolio.domain.entity.CodingAchievement;
import com.pranit.workspace.portfolio.domain.enums.CodingPlatform;
import lombok.Builder;

import java.util.UUID;

@Builder
public record CodingAchievementResponse(
        Long id,
        UUID userId,
        CodingPlatform platform,
        Integer problemsSolved,
        String profileLink,
        Integer displayOrder
) {
    public static CodingAchievementResponse from(final CodingAchievement achievement) {
        return CodingAchievementResponse.builder()
                .id(achievement.getId())
                .userId(achievement.getUserId())
                .platform(achievement.getPlatform())
                .problemsSolved(achievement.getProblemsSolved())
                .profileLink(achievement.getProfileLink())
                .displayOrder(achievement.getDisplayOrder())
                .build();
    }
}
