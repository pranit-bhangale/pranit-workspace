package com.pranit.workspace.portfolio.domain.dto;

import com.pranit.workspace.portfolio.domain.enums.DegreeType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record CreateEducationRequest(

        @NotBlank(message = "Degree title is required")
        @Size(max = 150, message = "Degree title must not exceed 150 characters")
        String degreeTitle,

        @NotNull(message = "Degree type is required")
        DegreeType degreeType,

        @NotBlank(message = "Institution name is required")
        @Size(max = 200, message = "Institution name must not exceed 200 characters")
        String institution,

        @NotNull(message = "Start date is required")
        LocalDate startDate,

        LocalDate endDate,

        @Size(max = 100, message = "Period display must not exceed 100 characters")
        String periodDisplay,

        @NotNull(message = "Current education status is required")
        Boolean isCurrent,

        @Size(max = 50, message = "Grade value must not exceed 50 characters")
        String gradeValue,

        @Size(max = 50, message = "Grade scale must not exceed 50 characters")
        String gradeScale,

        @Size(max = 100, message = "Distinction status must not exceed 100 characters")
        String distinctionStatus,

        @NotNull(message = "Display order is required")
        @PositiveOrZero(message = "Display order must be zero or greater")
        Integer displayOrder
) {
}
