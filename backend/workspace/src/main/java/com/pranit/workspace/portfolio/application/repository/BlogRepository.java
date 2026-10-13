package com.pranit.workspace.portfolio.application.repository;

import com.pranit.workspace.portfolio.domain.entity.BlogPost;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BlogRepository extends JpaRepository<BlogPost, Long>, JpaSpecificationExecutor<BlogPost> {

    @Override
    @EntityGraph(attributePaths = {"tags"})
    @NonNull
    Optional<BlogPost> findById(@NonNull Long id);

    boolean existsByTitleOrSlug(String title, String slug);
}
