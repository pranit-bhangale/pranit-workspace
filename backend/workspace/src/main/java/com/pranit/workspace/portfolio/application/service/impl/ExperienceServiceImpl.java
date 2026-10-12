package com.pranit.workspace.portfolio.application.service.impl;

import com.pranit.workspace.auth.security.helper.SecurityContext;
import com.pranit.workspace.portfolio.application.repository.ExperienceRepository;
import com.pranit.workspace.portfolio.application.repository.TechStackRepository;
import com.pranit.workspace.portfolio.application.service.ExperienceService;
import com.pranit.workspace.portfolio.application.specification.EntitySpecification;
import com.pranit.workspace.portfolio.domain.dto.CreateExperienceRequest;
import com.pranit.workspace.portfolio.domain.dto.CreateTechRequest;
import com.pranit.workspace.portfolio.domain.dto.ExperienceResponse;
import com.pranit.workspace.portfolio.domain.dto.TechStackResponse;
import com.pranit.workspace.portfolio.domain.dto.UpdateExperienceRequest;
import com.pranit.workspace.portfolio.domain.dto.UpdateTechRequest;
import com.pranit.workspace.portfolio.domain.entity.DatePeriod;
import com.pranit.workspace.portfolio.domain.entity.Experience;
import com.pranit.workspace.portfolio.domain.entity.TechStack;
import com.pranit.workspace.portfolio.domain.enums.EmploymentType;
import com.pranit.workspace.portfolio.domain.enums.WorkplaceType;
import com.pranit.workspace.shared.exception.ResourceNotFoundException;
import com.pranit.workspace.shared.helper.Pagination;
import com.pranit.workspace.shared.wrapper.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ExperienceServiceImpl implements ExperienceService {

    private final TechStackRepository techStackRepository;
    private final ExperienceRepository experienceRepository;

    @Override
    public List<WorkplaceType> getWorkplaceTypes() {
        return List.of(WorkplaceType.values());
    }

    @Override
    public List<EmploymentType> getEmploymentTypes() {
        return List.of(EmploymentType.values());
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void createNewTechStack(final CreateTechRequest request) {
        final TechStack techStack = TechStack.builder()
                .name(request.name())
                .build();
        this.techStackRepository.save(techStack);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public TechStackResponse getTechStackById(final Long id) {
        final TechStack techStack = validateAndFetchTechStackById(id);
        return TechStackResponse.from(techStack);
    }

    private TechStack validateAndFetchTechStackById(final Long id) {
        return this.techStackRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tech Stack Not Found"));
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public PageResponse<TechStackResponse> getAllTechStacks(final Pageable pageable) {
        return Pagination.paginate(this.techStackRepository, pageable, TechStackResponse::from);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public TechStackResponse updateTechStack(final Long id, final UpdateTechRequest request) {
        final TechStack techStack = validateAndFetchTechStackById(id);
        if (request.name() != null) techStack.setName(request.name());
        return TechStackResponse.from(techStack);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteTechStackById(final Long id) {
        final TechStack techStack = validateAndFetchTechStackById(id);
        this.techStackRepository.delete(techStack);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void createNewExperience(final CreateExperienceRequest request) {
        final UUID currentUserId = SecurityContext.getCurrentUserId();
        final DatePeriod period = DatePeriod.builder()
                .startDate(request.startDate())
                .endDate(request.endDate())
                .periodDisplay(request.periodDisplay())
                .isCurrent(request.isCurrent())
                .build();
        final List<TechStack> techStacks = request.techStacks().stream()
                .map(tech -> techStackRepository.findById(tech.id())
                        .orElseThrow(() -> new ResourceNotFoundException("Tech stack not found: " + tech.id())))
                .distinct()
                .toList();
        final Experience experience = Experience.builder()
                .userId(currentUserId)
                .company(request.company())
                .role(request.role())
                .employmentType(request.employmentType())
                .workplaceType(request.workplaceType())
                .period(period)
                .descriptionMarkdown(request.descriptionMarkdown())
                .techStacks(new ArrayList<>(techStacks))
                .displayOrder(request.displayOrder())
                .build();
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public ExperienceResponse getExperienceById(final Long id) {
        final Experience experience = validateAndFetchExperienceWithTechStackById(id);
        return ExperienceResponse.from(experience);
    }

    private Experience validateAndFetchExperienceWithTechStackById(final Long id) {
        return this.experienceRepository.findWithTechStacksById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Experience not found"));
    }

    private Experience validateAndFetchExperienceById(final Long id) {
        return this.experienceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Experience not found"));
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public PageResponse<ExperienceResponse> getAllExperiences
            (final int page, final int size, final String keyword,
             final String sortBy, final String sortDirection) {
        final Specification<Experience> specification = EntitySpecification.searchExperienceKeyword(keyword);
        return Pagination.paginate
                (this.experienceRepository, specification, page, size, sortBy, sortDirection, ExperienceResponse::from);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public ExperienceResponse updateExperienceById(final Long id, final UpdateExperienceRequest request) {
        final Experience experience = validateAndFetchExperienceById(id);
        if (request.company() != null) experience.setCompany(request.company());
        if (request.role() != null) experience.setRole(request.role());
        if (request.employmentType() != null) experience.setEmploymentType(request.employmentType());
        if (request.workplaceType() != null) experience.setWorkplaceType(request.workplaceType());
        if (request.descriptionMarkdown() != null) experience.setDescriptionMarkdown(request.descriptionMarkdown());
        if (request.displayOrder() != null) experience.setDisplayOrder(request.displayOrder());
        if (request.startDate() != null || request.endDate() != null
                || request.periodDisplay() != null || request.isCurrent() != null) {
            final DatePeriod period = experience.getPeriod();
            if (request.startDate() != null) period.setStartDate(request.startDate());
            if (request.endDate() != null) period.setEndDate(request.endDate());
            if (request.periodDisplay() != null) period.setPeriodDisplay(request.periodDisplay());
            if (request.isCurrent() != null) period.setIsCurrent(request.isCurrent());
            experience.setPeriod(period);
        }
        if (request.techStacks() != null) {
            experience.getTechStacks().clear();
            request.techStacks().stream()
                    .map(TechStackResponse::toEntity)
                    .forEach(experience.getTechStacks()::add);
        }
        return ExperienceResponse.from(experience);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteExperienceById(final Long id) {
        final Experience experience = validateAndFetchExperienceById(id);
        this.experienceRepository.delete(experience);
    }
}
