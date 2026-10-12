package com.pranit.workspace.portfolio.application.service.impl;

import com.pranit.workspace.portfolio.application.repository.FeatureRepository;
import com.pranit.workspace.portfolio.application.service.FeatureService;
import com.pranit.workspace.portfolio.domain.dto.CreateFeatureRequest;
import com.pranit.workspace.portfolio.domain.dto.FeatureResponse;
import com.pranit.workspace.portfolio.domain.dto.UpdateFeatureRequest;
import com.pranit.workspace.portfolio.domain.entity.Feature;
import com.pranit.workspace.shared.exception.ResourceNotFoundException;
import com.pranit.workspace.shared.helper.Pagination;
import com.pranit.workspace.shared.wrapper.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FeatureServiceImpl implements FeatureService {

    private final FeatureRepository featureRepository;

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void createNewFeature(final CreateFeatureRequest request) {
        final Feature feature = Feature.builder()
                .name(request.name())
                .description(request.description())
                .color(request.color())
                .build();
        this.featureRepository.save(feature);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteFeature(final Long id) {
        final Feature feature = this.validateAndFetchFeatureById(id);
        this.featureRepository.delete(feature);
    }

    private Feature validateAndFetchFeatureById(final Long id) {
        return this.featureRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Feature not found"));
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public FeatureResponse updateFeature(final Long id, final UpdateFeatureRequest request) {
        final Feature feature = this.validateAndFetchFeatureById(id);
        if (request.name() != null) feature.setName(request.name());
        if (request.description() != null) feature.setDescription(request.description());
        if (request.color() != null) feature.setColor(request.color());
        return FeatureResponse.from(feature);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public FeatureResponse getFeatureById(final Long id) {
        final Feature feature = this.validateAndFetchFeatureById(id);
        return FeatureResponse.from(feature);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public PageResponse<FeatureResponse> getFeatures(final Pageable pageable) {
        return Pagination.paginate(this.featureRepository, pageable, FeatureResponse::from);
    }
}
