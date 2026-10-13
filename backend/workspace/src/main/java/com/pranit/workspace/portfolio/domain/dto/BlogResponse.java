package com.pranit.workspace.portfolio.domain.dto;

import com.pranit.workspace.portfolio.domain.entity.BlogPost;
import com.pranit.workspace.portfolio.domain.enums.PostStatus;
import lombok.Builder;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Builder
public record BlogResponse(
        Long id,
        UUID userId,
        String title,
        String slug,
        String excerpt,
        String contentMarkdown,
        LocalDate publishDate,
        PostStatus status,
        List<TagResponse> tags,
        String externalLink,
        Integer displayOrder,
        Instant createdAt,
        Instant updatedAt
) {
    public static BlogResponse from(final BlogPost blogPost) {
        return BlogResponse.builder()
                .id(blogPost.getId())
                .userId(blogPost.getUserId())
                .title(blogPost.getTitle())
                .slug(blogPost.getSlug())
                .excerpt(blogPost.getExcerpt())
                .contentMarkdown(blogPost.getContentMarkdown())
                .publishDate(blogPost.getPublishDate())
                .status(blogPost.getStatus())
                .tags(blogPost.getTags().stream()
                        .map(TagResponse::from)
                        .toList())
                .externalLink(blogPost.getExternalLink())
                .displayOrder(blogPost.getDisplayOrder())
                .createdAt(blogPost.getCreatedAt())
                .updatedAt(blogPost.getUpdatedAt())
                .build();
    }
}
