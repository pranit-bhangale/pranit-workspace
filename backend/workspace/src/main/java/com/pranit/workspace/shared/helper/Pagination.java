package com.pranit.workspace.shared.helper;

import com.pranit.workspace.shared.wrapper.PageResponse;
import lombok.experimental.UtilityClass;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.function.Function;

@UtilityClass
public class Pagination {

    public static final int DEFAULT_PAGE_SIZE = 20;
    public static final int MAX_PAGE_SIZE = 100;

    private static Pageable clamp(final Pageable pageable) {
        if (pageable == null || pageable.isUnpaged()) return PageRequest.of(0, DEFAULT_PAGE_SIZE);
        final int page = Math.max(0, pageable.getPageNumber());
        final int size = Math.clamp(pageable.getPageSize(), 1, MAX_PAGE_SIZE);
        return PageRequest.of(page, size, pageable.getSort());
    }

    public static <E, D> PageResponse<D> paginate
            (final JpaRepository<E, ?> repository, final Pageable pageable, final Function<E, D> mapper) {
        final Page<E> result = repository.findAll(clamp(pageable));
        return PageResponse.from(result, mapper);
    }

    public static <E, D> PageResponse<D> paginate
            (final JpaSpecificationExecutor<E> repository, final Specification<E> specification,
             final int page, final int size, final String sortBy,
             final String sortDirection, final Function<E, D> mapper) {
        final Sort.Direction direction = "asc".equalsIgnoreCase(sortDirection)
                ? Sort.Direction.ASC : Sort.Direction.DESC;
        final Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        final Page<E> result = repository.findAll(specification, pageable);
        return PageResponse.from(result, mapper);
    }
}
