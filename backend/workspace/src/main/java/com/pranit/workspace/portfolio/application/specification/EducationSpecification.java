package com.pranit.workspace.portfolio.application.specification;

import com.pranit.workspace.portfolio.domain.entity.Education;
import lombok.experimental.UtilityClass;
import org.springframework.data.jpa.domain.Specification;

import java.util.Locale;

@UtilityClass
public class EducationSpecification {

    public static Specification<Education> searchKeyword(final String keyword) {
        return (root, query, cb) -> {
            if (keyword == null || keyword.isBlank()) return cb.conjunction();
            final String likePattern = "%" + keyword.trim().toLowerCase(Locale.ROOT) + "%";
            return cb.or(cb.like(cb.lower(root.get("degreeTitle")), likePattern));
        };
    }
}
