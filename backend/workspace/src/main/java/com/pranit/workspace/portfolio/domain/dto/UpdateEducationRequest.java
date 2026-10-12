package com.pranit.workspace.portfolio.domain.dto;

import com.pranit.workspace.portfolio.domain.enums.DegreeType;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record UpdateEducationRequest(
        @Size(max = 200, message = "Degree title must not exceed 200 characters")
        String degreeTitle,
        DegreeType degreeType,
        @Size(max = 200, message = "Institution name must not exceed 200 characters")
        String institution,
        LocalDate startDate,
        LocalDate endDate,
        @Size(max = 100, message = "Period display must not exceed 100 characters")
        String periodDisplay,
        Boolean isCurrent,
        @Size(max = 50, message = "Grade value must not exceed 50 characters")
        String gradeValue,
        @Size(max = 50, message = "Grade scale must not exceed 50 characters")
        String gradeScale,
        @Size(max = 100, message = "Distinction status must not exceed 100 characters")
        String distinctionStatus,
        @PositiveOrZero(message = "Display order must be zero or greater")
        Integer displayOrder
) {

    @AssertTrue(message = "End date must not be before start date")
    public boolean isEndDateValid() {
        return startDate == null || endDate == null || !endDate.isBefore(startDate);
    }

    @AssertTrue(message = "End date must be null for current education")
    public boolean isCurrentEducationValid() {
        return !Boolean.TRUE.equals(isCurrent) || endDate == null;
    }
}
