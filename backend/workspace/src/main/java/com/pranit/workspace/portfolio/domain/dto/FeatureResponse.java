package com.pranit.workspace.portfolio.domain.dto;

import com.pranit.workspace.portfolio.domain.entity.Feature;
import com.pranit.workspace.portfolio.domain.enums.Color;
import lombok.Builder;

@Builder
public record FeatureResponse(
        Long id,
        String name,
        String description,
        Color color
) {
    public static FeatureResponse from(final Feature feature) {
        return FeatureResponse.builder()
                .id(feature.getId())
                .name(feature.getName())
                .description(feature.getDescription())
                .color(feature.getColor())
                .build();
    }
}