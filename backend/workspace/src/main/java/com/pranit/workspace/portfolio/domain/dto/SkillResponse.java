package com.pranit.workspace.portfolio.domain.dto;

import com.pranit.workspace.portfolio.domain.entity.Skill;
import com.pranit.workspace.portfolio.domain.enums.ProficiencyLevel;
import com.pranit.workspace.portfolio.domain.enums.SkillCategory;
import lombok.Builder;

import java.util.UUID;

@Builder
public record SkillResponse(
        Long id,
        UUID userId,
        String name,
        SkillCategory category,
        ProficiencyLevel level,
        Integer displayOrder
) {
    public static SkillResponse from(final Skill skill) {
        return SkillResponse.builder()
                .id(skill.getId())
                .userId(skill.getUserId())
                .name(skill.getName())
                .category(skill.getCategory())
                .level(skill.getProficiencyLevel())
                .displayOrder(skill.getDisplayOrder())
                .build();
    }
}
