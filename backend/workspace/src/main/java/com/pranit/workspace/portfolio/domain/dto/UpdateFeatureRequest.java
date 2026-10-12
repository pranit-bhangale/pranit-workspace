package com.pranit.workspace.portfolio.domain.dto;

import com.pranit.workspace.portfolio.domain.enums.Color;
import jakarta.validation.constraints.Size;

public record UpdateFeatureRequest(
        @Size(max = 100, message = "Feature name must not exceed 100 characters")
        String name,
        String description,
        Color color
) {
}