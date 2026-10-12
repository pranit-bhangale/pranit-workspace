package com.pranit.workspace.portfolio.domain.dto;

import com.pranit.workspace.portfolio.domain.entity.TechStack;
import lombok.Builder;

@Builder
public record TechStackResponse(
        Long id,
        String name
) {
    public static TechStackResponse from(final TechStack techStack) {
        return TechStackResponse.builder()
                .id(techStack.getId())
                .name(techStack.getName())
                .build();
    }

    public TechStack toEntity() {
        return TechStack.builder()
                .name(this.name)
                .build();
    }
}
