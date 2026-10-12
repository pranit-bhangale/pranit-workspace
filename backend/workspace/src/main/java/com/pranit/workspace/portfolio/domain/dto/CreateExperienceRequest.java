package com.pranit.workspace.portfolio.domain.dto;

import com.pranit.workspace.portfolio.domain.enums.EmploymentType;
import com.pranit.workspace.portfolio.domain.enums.WorkplaceType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.List;

public record CreateExperienceRequest(
        @NotBlank(message = "Company is required")
        @Size(max = 150, message = "Company must not exceed 150 characters")
        String company,

        @NotBlank(message = "Role is required")
        @Size(max = 100, message = "Role must not exceed 100 characters")
        String role,

        @NotNull(message = "Employment type is required")
        EmploymentType employmentType,

        @NotNull(message = "Workplace type is required")
        WorkplaceType workplaceType,

        @NotNull(message = "Start date is required")
        LocalDate startDate,

        LocalDate endDate,

        @Size(max = 100, message = "Period display must not exceed 100 characters")
        String periodDisplay,

        @NotNull(message = "Current employment status is required")
        Boolean isCurrent,

        String descriptionMarkdown,

        @NotNull(message = "Tech stacks list is required")
        List<@Valid TechStackResponse> techStacks,

        @NotNull(message = "Display order is required")
        @PositiveOrZero(message = "Display order must be zero or greater")
        Integer displayOrder
) {

    @AssertTrue(message = "End date must not be before start date")
    public boolean isEndDateValid() {
        return startDate == null || endDate == null || !endDate.isBefore(startDate);
    }

    @AssertTrue(message = "End date must be null for current employment")
    public boolean isCurrentEmploymentValid() {
        return !Boolean.TRUE.equals(isCurrent) || endDate == null;
    }
}
