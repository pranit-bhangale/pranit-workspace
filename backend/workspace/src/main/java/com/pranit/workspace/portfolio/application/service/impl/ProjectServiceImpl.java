package com.pranit.workspace.portfolio.application.service.impl;

import com.pranit.workspace.portfolio.application.repository.FeatureRepository;
import com.pranit.workspace.portfolio.application.repository.ProjectRepository;
import com.pranit.workspace.portfolio.application.repository.TagRepository;
import com.pranit.workspace.portfolio.application.repository.TechStackRepository;
import com.pranit.workspace.portfolio.application.service.ProjectService;
import com.pranit.workspace.portfolio.application.specification.EntitySpecification;
import com.pranit.workspace.portfolio.domain.dto.CreateProjectRequest;
import com.pranit.workspace.portfolio.domain.dto.ProjectResponse;
import com.pranit.workspace.portfolio.domain.dto.UpdateProjectRequest;
import com.pranit.workspace.portfolio.domain.entity.Feature;
import com.pranit.workspace.portfolio.domain.entity.Project;
import com.pranit.workspace.portfolio.domain.entity.ProjectAssets;
import com.pranit.workspace.portfolio.domain.entity.Tag;
import com.pranit.workspace.portfolio.domain.entity.TechStack;
import com.pranit.workspace.portfolio.domain.enums.ProjectCategory;
import com.pranit.workspace.portfolio.domain.enums.ProjectStatus;
import com.pranit.workspace.shared.exception.ResourceNotFoundException;
import com.pranit.workspace.shared.helper.Pagination;
import com.pranit.workspace.shared.helper.SecurityContext;
import com.pranit.workspace.shared.wrapper.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final TechStackRepository techStackRepository;
    private final FeatureRepository featureRepository;
    private final TagRepository tagRepository;

    @Override
    public List<ProjectCategory> getCategories() {
        return List.of(ProjectCategory.values());
    }

    @Override
    public List<ProjectStatus> getStatuses() {
        return List.of(ProjectStatus.values());
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void createNewProject(final CreateProjectRequest request) {
        final UUID userId = SecurityContext.getCurrentUserId();
        final List<TechStack> techStacks = resolveTechStacks(request.techStackIds());
        final List<Feature> features = resolveFeatures(request.featureIds());
        final List<Tag> tags = resolveTags(request.tagIds());
        final ProjectAssets assets;
        if (request.assets() != null) {
            assets = ProjectAssets.builder()
                    .demoLink(request.assets().demoLink())
                    .githubLink(request.assets().githubLink())
                    .videoUrl(request.assets().videoUrl())
                    .imageKey(request.assets().imageKey())
                    .build();
        } else {
            assets = null;
        }

        final Project project = Project.builder()
                .userId(userId)
                .title(request.title().trim())
                .category(request.category())
                .status(request.status())
                .description(request.description().trim())
                .assets(assets)
                .techStacks(new ArrayList<>(techStacks))
                .features(new ArrayList<>(features))
                .tags(new ArrayList<>(tags))
                .isFeatured(Boolean.TRUE.equals(request.isFeatured()))
                .displayOrder(request.displayOrder() != null ? request.displayOrder() : 0)
                .build();
        this.projectRepository.save(project);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public ProjectResponse getProjectById(final Long id) {
        final Project project = validateAndFetchProjectById(id);
        return ProjectResponse.from(project);
    }

    private Project validateAndFetchProjectById(final Long id) {
        return this.projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project with id " + id + " not found"));
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public PageResponse<ProjectResponse> getProjects
            (final int page, final int size, final String keyword,
             final ProjectCategory category, final ProjectStatus status,
             final Boolean isFeatured, final String sortBy, final String sortDirection) {
        final Specification<Project> specification = Specification
                .where(EntitySpecification.searchProjectKeyword(keyword))
                .and(EntitySpecification.hasProjectCategory(category))
                .and(EntitySpecification.hasProjectStatus(status))
                .and(EntitySpecification.isProjectFeatured(isFeatured));
        return Pagination.paginate
                (this.projectRepository, specification, page, size, sortBy, sortDirection, ProjectResponse::from);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public ProjectResponse updateProject(final Long id, final UpdateProjectRequest request) {
        final Project project = validateAndFetchProjectById(id);
        if (request.title() != null && !request.title().isBlank()) project.setTitle(request.title().trim());
        if (request.category() != null) project.setCategory(request.category());
        if (request.status() != null) project.setStatus(request.status());
        if (request.description() != null && !request.description().isBlank())
            project.setDescription(request.description().trim());
        if (request.isFeatured() != null) project.setIsFeatured(request.isFeatured());
        if (request.displayOrder() != null) project.setDisplayOrder(request.displayOrder());
        if (request.assets() != null) {
            ProjectAssets assets = project.getAssets();
            if (request.assets().demoLink() != null) assets.setDemoLink(request.assets().demoLink());
            if (request.assets().githubLink() != null) assets.setGithubLink(request.assets().githubLink());
            if (request.assets().videoUrl() != null) assets.setVideoUrl(request.assets().videoUrl());
            if (request.assets().imageKey() != null) assets.setImageKey(request.assets().imageKey());
            project.setAssets(assets);
        }
        if (request.techStackIds() != null) {
            final List<TechStack> techStacks = resolveTechStacks(request.techStackIds());
            project.getTechStacks().clear();
            project.getTechStacks().addAll(techStacks);
        }
        if (request.featureIds() != null) {
            final List<Feature> features = resolveFeatures(request.featureIds());
            project.getFeatures().clear();
            project.getFeatures().addAll(features);
        }
        if (request.tagIds() != null) {
            final List<Tag> tags = resolveTags(request.tagIds());
            project.getTags().clear();
            project.getTags().addAll(tags);
        }
        return ProjectResponse.from(project);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteProject(final Long id) {
        final Project project = validateAndFetchProjectById(id);
        this.projectRepository.delete(project);
    }

    private List<TechStack> resolveTechStacks(final List<Long> ids) {
        if (ids == null || ids.isEmpty()) return List.of();
        final List<Long> distinctIds = ids.stream().distinct().toList();
        final List<TechStack> list = this.techStackRepository.findAllById(distinctIds);
        if (list.size() != distinctIds.size()) {
            throw new ResourceNotFoundException("One or more tech stacks were not found");
        }
        return list;
    }

    private List<Feature> resolveFeatures(final List<Long> ids) {
        if (ids == null || ids.isEmpty()) return List.of();
        final List<Long> distinctIds = ids.stream().distinct().toList();
        final List<Feature> list = this.featureRepository.findAllById(distinctIds);
        if (list.size() != distinctIds.size()) {
            throw new ResourceNotFoundException("One or more features were not found");
        }
        return list;
    }

    private List<Tag> resolveTags(final List<Long> ids) {
        if (ids == null || ids.isEmpty()) return List.of();
        final List<Long> distinctIds = ids.stream().distinct().toList();
        final List<Tag> list = this.tagRepository.findAllById(distinctIds);
        if (list.size() != distinctIds.size()) {
            throw new ResourceNotFoundException("One or more tags were not found");
        }
        return list;
    }
}
