package com.pranit.workspace.portfolio.domain.dto;

import com.pranit.workspace.portfolio.domain.enums.Color;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateTagRequest(

        @NotBlank(message = "Tag name is required")
        @Size(max = 50, message = "Tag name must not exceed 50 characters")
        String name,

        @NotNull(message = "Tag color is required")
        Color color

) {
}
