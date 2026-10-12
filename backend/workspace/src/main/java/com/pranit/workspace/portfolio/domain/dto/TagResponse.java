package com.pranit.workspace.portfolio.domain.dto;

import com.pranit.workspace.portfolio.domain.entity.Tag;
import com.pranit.workspace.portfolio.domain.enums.Color;
import lombok.Builder;

@Builder
public record TagResponse(
        Long id,
        String name,
        Color color
) {
    public static TagResponse from(final Tag tag) {
        return TagResponse.builder()
                .id(tag.getId())
                .name(tag.getName())
                .color(tag.getColor())
                .build();
    }
}