package com.pranit.workspace.portfolio.application.repository;

import com.pranit.workspace.portfolio.domain.entity.Project;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long>, JpaSpecificationExecutor<Project> {

    @Override
    @EntityGraph(attributePaths = {"techStacks", "features", "tags"})
    @NonNull
    Optional<Project> findById(@NonNull Long id);

    boolean existsByTitle(String title);
}
