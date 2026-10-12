package com.pranit.workspace.portfolio.domain.dto;

import com.pranit.workspace.portfolio.domain.enums.Color;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateFeatureRequest(

        @NotBlank(message = "Feature name is required")
        @Size(max = 100, message = "Feature name must not exceed 100 characters")
        String name,

        @NotBlank(message = "Feature description is required")
        String description,

        @NotNull(message = "Feature color is required")
        Color color
) {
}
