package com.pranit.workspace.portfolio.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Aggregated portfolio showcase response containing all sections for public display")
public record PortfolioOverviewResponse(
        @Schema(description = "Featured showcase projects")
        List<ProjectResponse> featuredProjects,

        @Schema(description = "Chronological professional work experiences")
        List<ExperienceResponse> experiences,

        @Schema(description = "Catalog of technical skills and proficiencies")
        List<SkillResponse> skills,

        @Schema(description = "Formal academic qualifications and certifications")
        List<EducationDetailResponse> educations,

        @Schema(description = "Competitive programming milestones and platform achievements")
        List<CodingAchievementResponse> achievements
) {
}
