package com.pranit.workspace.portfolio.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "High-level portfolio statistical indicators and headline metrics")
public record PortfolioSummaryResponse(
        @Schema(description = "Total number of showcased projects", example = "12")
        long totalProjects,

        @Schema(description = "Total number of documented skills", example = "28")
        long totalSkills,

        @Schema(description = "Total number of professional roles held", example = "4")
        long totalExperiences,

        @Schema(description = "Total competitive programming platforms featured", example = "3")
        long totalAchievements,

        @Schema(description = "Cumulative number of algorithmic challenges solved", example = "750")
        long totalProblemsSolved
) {
}
