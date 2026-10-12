package com.pranit.workspace.portfolio.domain.dto;

import com.pranit.workspace.portfolio.domain.enums.DegreeType;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record UpdateEducationRequest(
        @Size(max = 100, message = "Skill name must not exceed 100 characters")
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
        @PositiveOrZero(message = "Display order must be zero or greater")
        Integer displayOrder
) {
}
