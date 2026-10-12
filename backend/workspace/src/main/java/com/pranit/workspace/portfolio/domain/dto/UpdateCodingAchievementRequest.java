package com.pranit.workspace.portfolio.domain.dto;

import com.pranit.workspace.portfolio.domain.enums.CodingPlatform;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record CreateCodingAchievementRequest(

        @NotNull(message = "Coding platform is required")
        CodingPlatform platform,

        @NotNull(message = "Problems solved count is required")
        @PositiveOrZero(message = "Problems solved count must be zero or greater")
        Integer problemsSolved,

        @NotBlank(message = "Profile link is required")
        @Size(max = 2048, message = "Profile link must not exceed 2048 characters")
        String profileLink,

        @NotNull(message = "Display order is required")
        @PositiveOrZero(message = "Display order must be zero or greater")
        Integer displayOrder
) {
}
