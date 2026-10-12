package com.pranit.workspace.portfolio.application.service.impl;

import com.pranit.workspace.auth.security.helper.SecurityContext;
import com.pranit.workspace.portfolio.application.repository.EducationRepository;
import com.pranit.workspace.portfolio.application.service.EducationService;
import com.pranit.workspace.portfolio.application.specification.EducationSpecification;
import com.pranit.workspace.portfolio.domain.dto.CreateEducationRequest;
import com.pranit.workspace.portfolio.domain.dto.EducationDetailResponse;
import com.pranit.workspace.portfolio.domain.dto.UpdateEducationRequest;
import com.pranit.workspace.portfolio.domain.entity.AcademicEvaluation;
import com.pranit.workspace.portfolio.domain.entity.DatePeriod;
import com.pranit.workspace.portfolio.domain.entity.Education;
import com.pranit.workspace.portfolio.domain.enums.DegreeType;
import com.pranit.workspace.shared.exception.InvalidRequestException;
import com.pranit.workspace.shared.exception.ResourceNotFoundException;
import com.pranit.workspace.shared.helper.Pagination;
import com.pranit.workspace.shared.wrapper.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EducationServiceImpl implements EducationService {

    private final EducationRepository educationRepository;

    @Override
    public List<DegreeType> getDegrees() {
        return List.of(DegreeType.values());
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void createNewEducationDetail(final CreateEducationRequest request) {
        if (this.educationRepository.existsByDegreeTitle(request.degreeTitle())) {
            throw new InvalidRequestException("Degree Title already exists");
        }
        final UUID userId = SecurityContext.getCurrentUserId();
        final DatePeriod period = DatePeriod.builder()
                .startDate(request.startDate())
                .endDate(request.endDate())
                .periodDisplay(request.periodDisplay())
                .isCurrent(request.isCurrent())
                .build();
        final AcademicEvaluation evaluation = AcademicEvaluation.builder()
                .gradeValue(request.gradeValue())
                .gradeScale(request.gradeScale())
                .distinctionStatus(request.distinctionStatus())
                .build();
        final Education education = Education.builder()
                .userId(userId)
                .degreeTitle(request.degreeTitle())
                .degreeType(request.degreeType())
                .institution(request.institution())
                .period(period)
                .evaluation(evaluation)
                .displayOrder(request.displayOrder())
                .build();
        this.educationRepository.save(education);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public EducationDetailResponse getEducationDetailById(final Long id) {
        final Education education = validateAndFetchEducationDetailById(id);
        return EducationDetailResponse.from(education);
    }

    private Education validateAndFetchEducationDetailById(final Long id) {
        return this.educationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Education detail not found by id " + id));
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public PageResponse<EducationDetailResponse> getEducationDetails
            (final int page, final int size, final String keyword,
             final String sortBy, final String sortDirection) {
        final Specification<Education> specification = EducationSpecification.searchKeyword(keyword);
        return Pagination.paginate
                (this.educationRepository, specification, page, size, sortBy, sortDirection, EducationDetailResponse::from);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public EducationDetailResponse updateEducationDetails(final Long id, final UpdateEducationRequest request) {
        final Education education = validateAndFetchEducationDetailById(id);
        if (request.degreeTitle() != null) education.setDegreeTitle(request.degreeTitle());
        if (request.degreeType() != null) education.setDegreeType(request.degreeType());
        if (request.institution() != null) education.setInstitution(request.institution());
        if (request.displayOrder() != null) education.setDisplayOrder(request.displayOrder());
        if (request.startDate() != null || request.endDate() != null
                || request.periodDisplay() != null || request.isCurrent() != null) {
            final DatePeriod period = education.getPeriod();
            if (request.startDate() != null) period.setStartDate(request.startDate());
            if (request.endDate() != null) period.setEndDate(request.endDate());
            if (request.periodDisplay() != null) period.setPeriodDisplay(request.periodDisplay());
            if (request.isCurrent() != null) period.setIsCurrent(request.isCurrent());
            education.setPeriod(period);
        }
        if (request.gradeValue() != null || request.gradeScale() != null
                || request.distinctionStatus() != null) {
            final AcademicEvaluation evaluation = education.getEvaluation();
            if (request.gradeValue() != null) evaluation.setGradeValue(request.gradeValue());
            if (request.gradeScale() != null) evaluation.setGradeScale(request.gradeScale());
            if (request.distinctionStatus() != null) evaluation.setDistinctionStatus(request.distinctionStatus());
            education.setEvaluation(evaluation);
        }
        return EducationDetailResponse.from(education);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteEducationDetail(final Long id) {
        final Education education = validateAndFetchEducationDetailById(id);
        this.educationRepository.delete(education);
    }
}
