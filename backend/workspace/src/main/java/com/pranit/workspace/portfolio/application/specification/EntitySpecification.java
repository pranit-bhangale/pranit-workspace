package com.pranit.workspace.portfolio.application.specification;

import com.pranit.workspace.portfolio.domain.entity.BlogPost;
import com.pranit.workspace.portfolio.domain.entity.ContactMessage;
import com.pranit.workspace.portfolio.domain.entity.Education;
import com.pranit.workspace.portfolio.domain.entity.Experience;
import com.pranit.workspace.portfolio.domain.entity.Project;
import com.pranit.workspace.portfolio.domain.entity.Skill;
import com.pranit.workspace.portfolio.domain.enums.MessageStatus;
import com.pranit.workspace.portfolio.domain.enums.PostStatus;
import com.pranit.workspace.portfolio.domain.enums.ProjectCategory;
import com.pranit.workspace.portfolio.domain.enums.ProjectStatus;
import lombok.experimental.UtilityClass;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.Locale;

@UtilityClass
public class EntitySpecification {

    public static Specification<ContactMessage> searchContactMessageKeyword(final String keyword) {
        return (root, query, cb) -> {
            if (keyword == null || keyword.isBlank()) return cb.conjunction();
            final String likePattern = "%" + keyword.trim().toLowerCase(Locale.ROOT) + "%";
            return cb.or(
                    cb.like(cb.lower(root.get("messageContent")), likePattern),
                    cb.like(cb.lower(root.get("sender").get("name")), likePattern),
                    cb.like(cb.lower(root.get("sender").get("email")), likePattern)
            );
        };
    }

    public static Specification<ContactMessage> hasStatus(final MessageStatus status) {
        return (root, query, cb) -> {
            if (status == null) return cb.conjunction();
            return cb.equal(root.get("status"), status);
        };
    }

    public static Specification<Skill> searchSkillKeyword(final String keyword) {
        return (root, query, cb) -> {
            if (keyword == null || keyword.isBlank()) return cb.conjunction();
            final String likePattern = "%" + keyword.trim().toLowerCase(Locale.ROOT) + "%";
            return cb.or(cb.like(cb.lower(root.get("name")), likePattern));
        };
    }

    public static Specification<Education> searchEducationKeyword(final String keyword) {
        return (root, query, cb) -> {
            if (keyword == null || keyword.isBlank()) return cb.conjunction();
            final String likePattern = "%" + keyword.trim().toLowerCase(Locale.ROOT) + "%";
            return cb.or(
                    cb.like(cb.lower(root.get("degreeTitle")), likePattern),
                    cb.like(cb.lower(root.get("institution")), likePattern)
            );
        };
    }

    public static Specification<Experience> searchExperienceKeyword(final String keyword) {
        return (root, query, cb) -> {
            if (keyword == null || keyword.isBlank()) return cb.conjunction();
            final String likePattern = "%" + keyword.trim().toLowerCase(Locale.ROOT) + "%";
            return cb.or(
                    cb.like(cb.lower(root.get("company")), likePattern),
                    cb.like(cb.lower(root.get("role")), likePattern)
            );
        };
    }

    public static Specification<Project> searchProjectKeyword(final String keyword) {
        return (root, query, cb) -> {
            if (keyword == null || keyword.isBlank()) return cb.conjunction();
            final String likePattern = "%" + keyword.trim().toLowerCase(Locale.ROOT) + "%";
            return cb.or(
                    cb.like(cb.lower(root.get("title")), likePattern),
                    cb.like(cb.lower(root.get("description")), likePattern)
            );
        };
    }

    public static Specification<Project> hasProjectCategory(final ProjectCategory category) {
        return (root, query, cb) -> {
            if (category == null) return cb.conjunction();
            return cb.equal(root.get("category"), category);
        };
    }

    public static Specification<Project> hasProjectStatus(final ProjectStatus status) {
        return (root, query, cb) -> {
            if (status == null) return cb.conjunction();
            return cb.equal(root.get("status"), status);
        };
    }

    public static Specification<Project> isProjectFeatured(final Boolean isFeatured) {
        return (root, query, cb) -> {
            if (isFeatured == null) return cb.conjunction();
            return cb.equal(root.get("isFeatured"), isFeatured);
        };
    }

    public static Specification<BlogPost> searchPostKeyword(final String keyword) {
        return (root, query, cb) -> {
            if (keyword == null || keyword.isBlank()) return cb.conjunction();
            final String likePattern = "%" + keyword.trim().toLowerCase(Locale.ROOT) + "%";
            return cb.or(cb.like(cb.lower(root.get("title")), likePattern),
                    cb.like(cb.lower(root.get("slug")), likePattern),
                    cb.like(cb.lower(root.get("excerpt")), likePattern),
                    cb.like(cb.lower(root.get("contentMarkdown")), likePattern));
        };
    }

    public static Specification<BlogPost> hasPostStatus(final PostStatus status) {
        return (root, query, cb) -> {
            if (status == null) return cb.conjunction();
            return cb.equal(root.get("status"), status);
        };
    }

    public static Specification<BlogPost> hasPublishDateAfter(final LocalDate publishDate) {
        return (root, query, cb) -> {
            if (publishDate == null) return cb.conjunction();
            return cb.greaterThanOrEqualTo(root.get("publishDate"), publishDate);
        };
    }

    public static Specification<BlogPost> hasPublishDateBefore(final LocalDate publishDate) {
        return (root, query, cb) -> {
            if (publishDate == null) return cb.conjunction();
            return cb.lessThanOrEqualTo(root.get("publishDate"), publishDate);
        };
    }
}
