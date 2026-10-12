package com.pranit.workspace.portfolio.domain.dto;

import com.pranit.workspace.portfolio.domain.enums.ProjectCategory;
import com.pranit.workspace.portfolio.domain.enums.ProjectStatus;

import java.util.List;
import java.util.UUID;

public record ProjectResponse(
        Long id,
        UUID userId,
        String title,
        ProjectCategory category,
        ProjectStatus status,
        String description,
        ProjectAssetsResponse assets,
        List<TechStackResponse> techStacks,
        List<FeatureResponse> features,
        Boolean isFeatured,
        List<TagResponse> tags,
        Integer displayOrder
) {
    public record ProjectAssetsResponse(
            String thumbnailUrl,
            String liveUrl,
            String repositoryUrl
    ) {
    }
}
