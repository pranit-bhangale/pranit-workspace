package com.pranit.workspace.portfolio.application.repository;

import com.pranit.workspace.portfolio.domain.entity.Experience;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ExperienceRepository extends JpaRepository<Experience, Long>, JpaSpecificationExecutor<Experience> {

    @Override
    @EntityGraph(attributePaths = "techStacks")
    @NonNull
    Optional<Experience> findById(@NonNull Long id);

    @Override
    @EntityGraph(attributePaths = "techStacks")
    @NonNull
    Page<Experience> findAll(@NonNull Specification<Experience> spec, @NonNull Pageable pageable);
}
