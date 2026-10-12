package com.pranit.workspace.portfolio.application.service;

import com.pranit.workspace.portfolio.domain.dto.CreateFeatureRequest;
import com.pranit.workspace.portfolio.domain.dto.FeatureResponse;
import com.pranit.workspace.portfolio.domain.dto.UpdateFeatureRequest;
import com.pranit.workspace.shared.wrapper.PageResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;

public interface FeatureService {

    void createNewFeature(@Valid CreateFeatureRequest request);

    void deleteFeature(Long id);

    FeatureResponse updateFeature(Long id, @Valid UpdateFeatureRequest request);

    FeatureResponse getFeatureById(Long id);

    PageResponse<FeatureResponse> getFeatures(Pageable pageable);
}
