package com.pranit.workspace.portfolio.application.service.impl;

import com.pranit.workspace.portfolio.application.repository.CodingAchievementRepository;
import com.pranit.workspace.portfolio.application.service.CodingAchievementService;
import com.pranit.workspace.portfolio.domain.dto.CodingAchievementResponse;
import com.pranit.workspace.portfolio.domain.dto.CreateCodingAchievementRequest;
import com.pranit.workspace.portfolio.domain.dto.UpdateCodingAchievementRequest;
import com.pranit.workspace.portfolio.domain.entity.CodingAchievement;
import com.pranit.workspace.portfolio.domain.enums.CodingPlatform;
import com.pranit.workspace.shared.exception.ResourceNotFoundException;
import com.pranit.workspace.shared.helper.Pagination;
import com.pranit.workspace.shared.helper.SecurityContext;
import com.pranit.workspace.shared.wrapper.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CodingAchievementServiceImpl implements CodingAchievementService {

    private final CodingAchievementRepository codingAchievementRepository;

    @Override
    public List<CodingPlatform> getCodingPlatforms() {
        return List.of(CodingPlatform.values());
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void createNewCodingAchievement(final CreateCodingAchievementRequest request) {
        final UUID currentUserId = SecurityContext.getCurrentUserId();
        final CodingAchievement achievement = CodingAchievement.builder()
                .userId(currentUserId)
                .platform(request.platform())
                .problemsSolved(request.problemsSolved())
                .profileLink(request.profileLink().trim())
                .displayOrder(request.displayOrder())
                .build();
        this.codingAchievementRepository.save(achievement);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public CodingAchievementResponse getAchievementById(final Long id) {
        final CodingAchievement achievement = validateAndFetchAchievementById(id);
        return CodingAchievementResponse.from(achievement);
    }

    private CodingAchievement validateAndFetchAchievementById(final Long id) {
        return this.codingAchievementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Coding Achievement with id " + id + " not found"));
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public PageResponse<CodingAchievementResponse> getCodingAchievements(final Pageable pageable) {
        return Pagination.paginate
                (this.codingAchievementRepository, pageable, CodingAchievementResponse::from);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public CodingAchievementResponse updateCodingAchievements(final Long id, final UpdateCodingAchievementRequest request) {
        final CodingAchievement achievement = validateAndFetchAchievementById(id);
        if (request.platform() != null) achievement.setPlatform(request.platform());
        if (request.problemsSolved() != null) achievement.setProblemsSolved(request.problemsSolved());
        if (request.profileLink() != null && !request.profileLink().isBlank())
            achievement.setProfileLink(request.profileLink().trim());

        if (request.displayOrder() != null) achievement.setDisplayOrder(request.displayOrder());
        return CodingAchievementResponse.from(achievement);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteCodingAchievement(final Long id) {
        final CodingAchievement achievement = validateAndFetchAchievementById(id);
        this.codingAchievementRepository.delete(achievement);
    }
}
