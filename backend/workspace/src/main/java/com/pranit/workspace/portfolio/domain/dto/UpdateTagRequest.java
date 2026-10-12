package com.pranit.workspace.portfolio.domain.dto;

import com.pranit.workspace.portfolio.domain.enums.Color;
import jakarta.validation.constraints.Size;

public record UpdateTagRequest(
        @Size(max = 50, message = "Tag name must not exceed 50 characters")
        String name,
        Color color

) {
}