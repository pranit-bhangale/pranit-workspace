package com.pranit.workspace.portfolio.domain.dto;

import com.pranit.workspace.portfolio.domain.entity.AcademicEvaluation;
import com.pranit.workspace.portfolio.domain.entity.DatePeriod;
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
        final DatePeriod period = education.getPeriod();
        final AcademicEvaluation evaluation = education.getEvaluation();

        return EducationDetailResponse.builder()
                .id(education.getId())
                .userId(education.getUserId())
                .degreeTitle(education.getDegreeTitle())
                .degreeType(education.getDegreeType())
                .institution(education.getInstitution())
                .startDate(period != null ? period.getStartDate() : null)
                .endDate(period != null ? period.getEndDate() : null)
                .periodDisplay(period != null ? period.getPeriodDisplay() : null)
                .isCurrent(period != null ? period.getIsCurrent() : null)
                .gradeValue(evaluation != null ? evaluation.getGradeValue() : null)
                .gradeScale(evaluation != null ? evaluation.getGradeScale() : null)
                .distinctionStatus(evaluation != null ? evaluation.getDistinctionStatus() : null)
                .displayOrder(education.getDisplayOrder())
                .build();
    }
}
