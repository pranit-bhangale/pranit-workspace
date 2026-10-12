package com.pranit.workspace.portfolio.domain.dto;

import com.pranit.workspace.portfolio.domain.entity.DatePeriod;
import com.pranit.workspace.portfolio.domain.entity.Experience;
import com.pranit.workspace.portfolio.domain.enums.EmploymentType;
import com.pranit.workspace.portfolio.domain.enums.WorkplaceType;
import lombok.Builder;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Builder
public record ExperienceResponse(
        Long id,
        UUID userId,
        String company,
        String role,
        EmploymentType employmentType,
        WorkplaceType workplaceType,
        LocalDate startDate,
        LocalDate endDate,
        String periodDisplay,
        Boolean isCurrent,
        String descriptionMarkdown,
        List<TechStackResponse> techStacks,
        Integer displayOrder
) {

    public static ExperienceResponse from(final Experience experience) {
        final DatePeriod period = experience.getPeriod();
        return ExperienceResponse.builder()
                .id(experience.getId())
                .userId(experience.getUserId())
                .company(experience.getCompany())
                .role(experience.getRole())
                .employmentType(experience.getEmploymentType())
                .workplaceType(experience.getWorkplaceType())
                .startDate(period != null ? period.getStartDate() : null)
                .endDate(period != null ? period.getEndDate() : null)
                .periodDisplay(period != null ? period.getPeriodDisplay() : null)
                .isCurrent(period != null ? period.getIsCurrent() : null)
                .descriptionMarkdown(experience.getDescriptionMarkdown())
                .techStacks(experience.getTechStacks() == null
                        ? List.of() : experience.getTechStacks().stream()
                        .map(TechStackResponse::from)
                        .toList())
                .displayOrder(experience.getDisplayOrder())
                .build();
    }
}
