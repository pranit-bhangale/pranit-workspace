package com.pranit.workspace.portfolio.application.service.impl;

import com.pranit.workspace.auth.security.helper.SecurityContext;
import com.pranit.workspace.portfolio.application.repository.SkillRepository;
import com.pranit.workspace.portfolio.application.service.SkillService;
import com.pranit.workspace.portfolio.application.specification.SkillSpecification;
import com.pranit.workspace.portfolio.domain.dto.CreateSkillRequest;
import com.pranit.workspace.portfolio.domain.dto.SkillResponse;
import com.pranit.workspace.portfolio.domain.dto.UpdateSkillRequest;
import com.pranit.workspace.portfolio.domain.entity.Skill;
import com.pranit.workspace.portfolio.domain.enums.ProficiencyLevel;
import com.pranit.workspace.portfolio.domain.enums.SkillCategory;
import com.pranit.workspace.shared.exception.ResourceNotFoundException;
import com.pranit.workspace.shared.helper.Pagination;
import com.pranit.workspace.shared.wrapper.PageResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class SkillServiceImpl implements SkillService {

    private final SkillRepository skillRepository;

    @Override
    public List<SkillCategory> getSkillCategories() {
        return List.of(SkillCategory.values());
    }

    @Override
    public List<ProficiencyLevel> getAllProficiencyLevel() {
        return List.of(ProficiencyLevel.values());
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void createNewSkill(final CreateSkillRequest request) {
        final UUID currentUserId = SecurityContext.getCurrentUserId();
        final Skill skill = Skill.builder()
                .userId(currentUserId)
                .name(request.name())
                .category(request.category())
                .proficiencyLevel(request.proficiencyLevel())
                .displayOrder(request.displayOrder())
                .build();
        this.skillRepository.save(skill);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public SkillResponse getSkillById(final Long id) {
        final Skill skill = validateAndFetchSkillById(id);
        return SkillResponse.from(skill);
    }

    private Skill validateAndFetchSkillById(final Long id) {
        return this.skillRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Skill with id " + id + " not found"));
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public PageResponse<SkillResponse> getSkills
            (final int page, final int size, final String keyword,
             final String sortBy, final String sortDirection) {
        final Specification<Skill> specification = SkillSpecification.searchKeyword(keyword);
        return Pagination.search
                (this.skillRepository, specification, page, size, sortBy, sortDirection, SkillResponse::from);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public SkillResponse updateSkill(final Long id, final UpdateSkillRequest request) {
        final Skill skill = validateAndFetchSkillById(id);
        if (request.name() != null && !request.name().isEmpty()) skill.setName(request.name());
        if (request.category() != null) skill.setCategory(request.category());
        if (request.proficiencyLevel() != null) skill.setProficiencyLevel(request.proficiencyLevel());
        if (request.displayOrder() != null) skill.setDisplayOrder(request.displayOrder());
        return SkillResponse.from(skill);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteSkill(final Long id) {
        final Skill skill = validateAndFetchSkillById(id);
        this.skillRepository.delete(skill);
    }
}
