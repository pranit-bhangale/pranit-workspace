package com.pranit.workspace.portfolio.domain.dto;

import com.pranit.workspace.portfolio.domain.enums.ProficiencyLevel;
import com.pranit.workspace.portfolio.domain.enums.SkillCategory;

public record UpdateSkillRequest(
        String name,
        SkillCategory category,
        ProficiencyLevel proficiencyLevel,
        Integer displayOrder
) {
}
