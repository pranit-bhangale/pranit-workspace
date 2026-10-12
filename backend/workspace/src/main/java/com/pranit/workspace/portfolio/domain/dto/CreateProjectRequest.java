package com.pranit.workspace.portfolio.domain.dto;

import com.pranit.workspace.portfolio.domain.enums.ProjectCategory;
import com.pranit.workspace.portfolio.domain.enums.ProjectStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record CreateProjectRequest(

        @NotBlank(message = "Project title is required")
        @Size(max = 150, message = "Title must not exceed 150 characters")
        String title,

        @NotNull(message = "Project category is required")
        ProjectCategory category,

        @NotNull(message = "Project status is required")
        ProjectStatus status,

        @NotBlank(message = "Project description is required")
        String description,

        @Valid
        @NotNull(message = "Project assets are required")
        ProjectAssetsRequest assets,

        List<Long> techStackIds,

        List<Long> featureIds,

        Boolean isFeatured,

        List<Long> tagIds,

        Integer displayOrder

) {
    public record ProjectAssetsRequest(
            String thumbnailUrl,
            String liveUrl,
            String repositoryUrl
    ) {
    }
}