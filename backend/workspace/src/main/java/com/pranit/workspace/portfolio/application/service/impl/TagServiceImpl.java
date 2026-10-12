package com.pranit.workspace.portfolio.application.service.impl;

import com.pranit.workspace.portfolio.application.repository.TagRepository;
import com.pranit.workspace.portfolio.application.service.TagService;
import com.pranit.workspace.portfolio.domain.dto.CreateTagRequest;
import com.pranit.workspace.portfolio.domain.dto.TagResponse;
import com.pranit.workspace.portfolio.domain.dto.UpdateTagRequest;
import com.pranit.workspace.portfolio.domain.entity.Tag;
import com.pranit.workspace.portfolio.domain.enums.Color;
import com.pranit.workspace.shared.exception.ResourceNotFoundException;
import com.pranit.workspace.shared.helper.Pagination;
import com.pranit.workspace.shared.wrapper.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TagServiceImpl implements TagService {

    private final TagRepository tagRepository;

    @Override
    public List<Color> getColors() {
        return List.of(Color.values());
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void createNewTag(final CreateTagRequest request) {
        final Tag tag = Tag.builder()
                .name(request.name())
                .color(request.color())
                .build();
        this.tagRepository.save(tag);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteTag(final Long id) {
        final Tag tag = validateAndFetchTagById(id);
        this.tagRepository.delete(tag);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public TagResponse updateTag(final Long id, final UpdateTagRequest request) {
        final Tag tag = validateAndFetchTagById(id);
        if (request.name() != null) tag.setName(request.name());
        if (request.color() != null) tag.setColor(request.color());
        return TagResponse.from(tag);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public TagResponse getTagById(final Long id) {
        final Tag tag = validateAndFetchTagById(id);
        return TagResponse.from(tag);
    }

    private Tag validateAndFetchTagById(final Long id) {
        return this.tagRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tag not found"));
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public PageResponse<TagResponse> getTags(final Pageable pageable) {
        return Pagination.paginate(this.tagRepository, pageable, TagResponse::from);
    }
}
