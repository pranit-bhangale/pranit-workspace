package com.pranit.workspace.portfolio.application.service;

import com.pranit.workspace.portfolio.domain.dto.CreateExperienceRequest;
import com.pranit.workspace.portfolio.domain.dto.CreateTechRequest;
import com.pranit.workspace.portfolio.domain.dto.ExperienceResponse;
import com.pranit.workspace.portfolio.domain.dto.TechStackResponse;
import com.pranit.workspace.portfolio.domain.dto.UpdateExperienceRequest;
import com.pranit.workspace.portfolio.domain.dto.UpdateTechRequest;
import com.pranit.workspace.portfolio.domain.enums.EmploymentType;
import com.pranit.workspace.portfolio.domain.enums.WorkplaceType;
import com.pranit.workspace.shared.wrapper.PageResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ExperienceService {

    List<WorkplaceType> getWorkplaceTypes();

    List<EmploymentType> getEmploymentTypes();

    void createNewTechStack(@Valid CreateTechRequest request);

    TechStackResponse getTechStackById(Long id);

    PageResponse<TechStackResponse> getAllTechStacks(Pageable pageable);

    TechStackResponse updateTechStack(Long id, @Valid UpdateTechRequest request);

    void deleteTechStackById(Long id);

    void createNewExperience(@Valid CreateExperienceRequest request);

    ExperienceResponse getExperienceById(Long id);

    PageResponse<ExperienceResponse> getAllExperiences
            (@Min(0) int page, @Min(1) @Max(5) int size, String keyword, String sortBy, String sortDirection);

    ExperienceResponse updateExperienceById(Long id, @Valid UpdateExperienceRequest request);

    void deleteExperienceById(Long id);

}
