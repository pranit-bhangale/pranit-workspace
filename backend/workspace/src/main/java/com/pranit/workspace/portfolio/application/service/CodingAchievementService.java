package com.pranit.workspace.portfolio.application.service;

import com.pranit.workspace.portfolio.domain.dto.CodingAchievementResponse;
import com.pranit.workspace.portfolio.domain.dto.CreateCodingAchievementRequest;
import com.pranit.workspace.portfolio.domain.dto.UpdateCodingAchievementRequest;
import com.pranit.workspace.portfolio.domain.enums.CodingPlatform;
import com.pranit.workspace.shared.wrapper.PageResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CodingAchievementService {

    List<CodingPlatform> getCodingPlatforms();

    void createNewCodingAchievement(@Valid CreateCodingAchievementRequest request);

    CodingAchievementResponse getAchievementById(Long id);

    PageResponse<CodingAchievementResponse> getCodingAchievements(Pageable pageable);

    CodingAchievementResponse updateCodingAchievements(Long id, @Valid UpdateCodingAchievementRequest request);

    void deleteCodingAchievement(Long id);
}
