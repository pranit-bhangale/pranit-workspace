package com.pranit.workspace.portfolio.domain.dto;

import com.pranit.workspace.portfolio.domain.enums.CodingPlatform;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record UpdateCodingAchievementRequest(
        CodingPlatform platform,
        @PositiveOrZero(message = "Problems solved count must be zero or greater")
        Integer problemsSolved,
        @Size(max = 2048, message = "Profile link must not exceed 2048 characters")
        String profileLink,
        @PositiveOrZero(message = "Display order must be zero or greater")
        Integer displayOrder
) {
}
