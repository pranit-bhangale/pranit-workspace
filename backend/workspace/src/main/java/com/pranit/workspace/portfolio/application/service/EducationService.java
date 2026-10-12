package com.pranit.workspace.portfolio.application.service;

import com.pranit.workspace.portfolio.domain.dto.CreateEducationRequest;
import com.pranit.workspace.portfolio.domain.dto.EducationDetailResponse;
import com.pranit.workspace.portfolio.domain.dto.UpdateEducationRequest;
import com.pranit.workspace.portfolio.domain.enums.DegreeType;
import com.pranit.workspace.shared.wrapper.PageResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

import java.util.List;

public interface EducationService {

    List<DegreeType> getDegrees();

    void createNewEducationDetail(@Valid CreateEducationRequest request);

    EducationDetailResponse getEducationDetailById(Long id);

    PageResponse<EducationDetailResponse> getEducationDetails
            (@Min(0) int page, @Min(1) @Max(5) int size, String keyword, String sortBy, String sortDirection);

    EducationDetailResponse updateEducationDetails(Long id, @Valid UpdateEducationRequest request);

    void deleteEducationDetail(Long id);
}
