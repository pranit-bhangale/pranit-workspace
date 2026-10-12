package com.pranit.workspace.portfolio.application.service;

import com.pranit.workspace.portfolio.domain.dto.CreateProjectRequest;
import com.pranit.workspace.portfolio.domain.dto.ProjectResponse;
import com.pranit.workspace.portfolio.domain.dto.UpdateProjectRequest;
import com.pranit.workspace.portfolio.domain.enums.ProjectCategory;
import com.pranit.workspace.portfolio.domain.enums.ProjectStatus;
import com.pranit.workspace.shared.wrapper.PageResponse;
import jakarta.validation.Valid;

import java.util.List;

public interface ProjectService {

    List<ProjectCategory> getCategories();

    List<ProjectStatus> getStatuses();

    void createNewProject(@Valid CreateProjectRequest request);

    ProjectResponse getProjectById(Long id);

    PageResponse<ProjectResponse> getProjects
            (int page, int size, String keyword, ProjectCategory category,
             ProjectStatus status, Boolean isFeatured, String sortBy, String sortDirection);

    ProjectResponse updateProject(Long id, @Valid UpdateProjectRequest request);

    void deleteProject(Long id);
}
