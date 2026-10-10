package com.pranit.workspace.shared.wrapper;

import lombok.Builder;

import org.springframework.data.domain.Page;

import java.util.List;
import java.util.function.Function;

@Builder
public record PageResponse<T>(
        List<T> contents,
        int currentPage,
        int pageSize,
        long totalElements,
        int totalPages,
        boolean isLastPage,
        boolean isFirstPage
) {
    public static <T> PageResponse<T> from(final Page<T> page) {
        return PageResponse.<T>builder()
                .contents(page.getContent())
                .currentPage(page.getNumber())
                .pageSize(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .isLastPage(page.isLast())
                .isFirstPage(page.isFirst())
                .build();
    }

    public static <S, T> PageResponse<T> from(final Page<S> page, final Function<S, T> mapper) {
        return PageResponse.<T>builder()
                .contents(page.getContent().stream().map(mapper).toList())
                .currentPage(page.getNumber())
                .pageSize(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .isLastPage(page.isLast())
                .isFirstPage(page.isFirst())
                .build();
    }
}
