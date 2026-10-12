package com.pranit.workspace.portfolio.domain.dto;

import com.pranit.workspace.portfolio.domain.entity.Project;
import com.pranit.workspace.portfolio.domain.entity.ProjectAssets;
import com.pranit.workspace.portfolio.domain.enums.ProjectCategory;
import com.pranit.workspace.portfolio.domain.enums.ProjectStatus;
import lombok.Builder;

import java.util.List;
import java.util.UUID;

@Builder
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
            String demoLink,
            String githubLink,
            String videoUrl,
            String imageKey
    ) {
    }

    public static ProjectResponse from(final Project project) {
        final ProjectAssets assets = project.getAssets();
        final ProjectAssetsResponse assetsResponse = assets != null
                ? new ProjectAssetsResponse(
                        assets.getDemoLink(),
                        assets.getGithubLink(),
                        assets.getVideoUrl(),
                        assets.getImageKey())
                : null;

        return ProjectResponse.builder()
                .id(project.getId())
                .userId(project.getUserId())
                .title(project.getTitle())
                .category(project.getCategory())
                .status(project.getStatus())
                .description(project.getDescription())
                .assets(assetsResponse)
                .techStacks(project.getTechStacks() == null ? List.of()
                        : project.getTechStacks().stream().map(TechStackResponse::from).toList())
                .features(project.getFeatures() == null ? List.of()
                        : project.getFeatures().stream().map(FeatureResponse::from).toList())
                .isFeatured(project.getIsFeatured())
                .tags(project.getTags() == null ? List.of()
                        : project.getTags().stream().map(TagResponse::from).toList())
                .displayOrder(project.getDisplayOrder())
                .build();
    }
}
