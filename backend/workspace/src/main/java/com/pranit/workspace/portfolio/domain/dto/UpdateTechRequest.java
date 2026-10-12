package com.pranit.workspace.portfolio.domain.dto;

import jakarta.validation.constraints.Size;

public record UpdateTechRequest(
        @Size(max = 100, message = "Skill name must not exceed 100 characters")
        String name
) {
}
