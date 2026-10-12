package com.pranit.workspace.portfolio.application.service;

import com.pranit.workspace.portfolio.domain.dto.CreateTagRequest;
import com.pranit.workspace.portfolio.domain.dto.TagResponse;
import com.pranit.workspace.portfolio.domain.dto.UpdateTagRequest;
import com.pranit.workspace.portfolio.domain.enums.Color;
import com.pranit.workspace.shared.wrapper.PageResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TagService {

    List<Color> getColors();

    void createNewTag(@Valid CreateTagRequest request);

    void deleteTag(Long id);

    TagResponse updateTag(Long id, @Valid UpdateTagRequest request);

    TagResponse getTagById(Long id);

    PageResponse<TagResponse> getTags(Pageable pageable);
}
