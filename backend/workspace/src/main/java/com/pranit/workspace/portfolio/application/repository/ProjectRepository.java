package com.pranit.workspace.portfolio.application.repository;

import com.pranit.workspace.portfolio.domain.entity.Project;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long>, JpaSpecificationExecutor<Project> {

    @EntityGraph(attributePaths = {"techStacks", "features", "tags"})
    Optional<Project> findWithDetailsById(Long id);

    boolean existsByTitle(String title);
}
