package com.pranit.workspace.portfolio.domain.dto;

import com.pranit.workspace.portfolio.domain.enums.PostStatus;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.List;

public record CreateBlogRequest(
        @NotBlank(message = "Title is required")
        @Size(max = 255, message = "Title must not exceed 255 characters")
        String title,

        @NotBlank(message = "Slug is required")
        @Size(max = 255, message = "Slug must not exceed 255 characters")
        @Pattern(regexp = "^[a-z0-9]+(?:-[a-z0-9]+)*$", message = "Slug must contain lowercase letters, numbers, and single hyphens only")
        String slug,

        @Size(max = 5000, message = "Excerpt must not exceed 5000 characters")
        String excerpt,

        String contentMarkdown,

        @FutureOrPresent(message = "Publish date cannot be in the past")
        LocalDate publishDate,

        @NotNull(message = "Post status is required")
        PostStatus status,

        List<@NotNull(message = "Tag ID cannot be null") @Positive(message = "Tag ID must be greater than zero") Long> tagIds,

        @Size(max = 2048, message = "External link must not exceed 2048 characters")
        @Pattern(regexp = "^(https?://.*)?$", message = "External link must start with http:// or https://")
        String externalLink,

        @NotNull(message = "Display order is required")
        @PositiveOrZero(message = "Display order cannot be negative")
        Integer displayOrder
) {
}
