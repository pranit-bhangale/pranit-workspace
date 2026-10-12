package com.pranit.workspace.portfolio.application.specification;

import com.pranit.workspace.portfolio.domain.entity.ContactMessage;
import com.pranit.workspace.portfolio.domain.enums.MessageStatus;
import lombok.experimental.UtilityClass;
import org.springframework.data.jpa.domain.Specification;

import java.util.Locale;

@UtilityClass
public class MessageSpecification {

    public static Specification<ContactMessage> searchKeyword(final String keyword) {
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
}
