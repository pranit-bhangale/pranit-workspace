package com.pranit.workspace.portfolio.domain.dto;

import com.pranit.workspace.portfolio.domain.entity.Education;
import com.pranit.workspace.portfolio.domain.enums.DegreeType;
import lombok.Builder;

import java.time.LocalDate;
import java.util.UUID;

@Builder
public record EducationDetailResponse(
        Long id,
        UUID userId,
        String degreeTitle,
        DegreeType degreeType,
        String institution,
        LocalDate startDate,
        LocalDate endDate,
        String periodDisplay,
        Boolean isCurrent,
        String gradeValue,
        String gradeScale,
        String distinctionStatus,
        Integer displayOrder
) {
    public static EducationDetailResponse from(final Education education) {
        return EducationDetailResponse.builder()
                .id(education.getId())
                .userId(education.getUserId())
                .degreeTitle(education.getDegreeTitle())
                .degreeType(education.getDegreeType())
                .institution(education.getInstitution())
                .startDate(education.getPeriod().getStartDate())
                .endDate(education.getPeriod().getEndDate())
                .periodDisplay(education.getPeriod().getPeriodDisplay())
                .isCurrent(education.getPeriod().getIsCurrent())
                .gradeValue(education.getEvaluation().getGradeValue())
                .gradeScale(education.getEvaluation().getGradeScale())
                .distinctionStatus(education.getEvaluation().getDistinctionStatus())
                .displayOrder(education.getDisplayOrder())
                .build();
    }
}
