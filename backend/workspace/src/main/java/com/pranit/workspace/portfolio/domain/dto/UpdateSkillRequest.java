package com.pranit.workspace.portfolio.domain.dto;

import com.pranit.workspace.portfolio.domain.enums.ProficiencyLevel;
import com.pranit.workspace.portfolio.domain.enums.SkillCategory;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record UpdateSkillRequest(
        @Size(max = 100, message = "Skill name must not exceed 100 characters")
        String name,
        SkillCategory category,
        ProficiencyLevel proficiencyLevel,
        @PositiveOrZero(message = "Display order must be zero or greater")
        Integer displayOrder
) {
}
