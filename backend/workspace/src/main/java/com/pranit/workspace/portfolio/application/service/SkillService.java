package com.pranit.workspace.portfolio.application.service;

import com.pranit.workspace.portfolio.domain.dto.CreateSkillRequest;
import com.pranit.workspace.portfolio.domain.dto.SkillResponse;
import com.pranit.workspace.portfolio.domain.dto.UpdateSkillRequest;
import com.pranit.workspace.portfolio.domain.enums.ProficiencyLevel;
import com.pranit.workspace.portfolio.domain.enums.SkillCategory;
import com.pranit.workspace.shared.wrapper.PageResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

import java.util.List;

public interface SkillService {

    List<SkillCategory> getSkillCategories();

    List<ProficiencyLevel> getAllProficiencyLevel();

    void createNewSkill(@Valid CreateSkillRequest request);

    SkillResponse getSkillById(Long id);

    PageResponse<SkillResponse> getSkills
            (@Min(0) int page, @Min(1) @Max(5) int size, String keyword, String sortBy, String sortDirection);

    SkillResponse updateSkill(Long id, @Valid UpdateSkillRequest request);

    void deleteSkill(Long id);
}
