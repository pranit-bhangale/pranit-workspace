package com.pranit.workspace.portfolio.controller;

import com.pranit.workspace.portfolio.application.service.FeatureService;
import com.pranit.workspace.portfolio.domain.dto.CreateFeatureRequest;
import com.pranit.workspace.portfolio.domain.dto.FeatureResponse;
import com.pranit.workspace.portfolio.domain.dto.UpdateFeatureRequest;
import com.pranit.workspace.shared.wrapper.ErrorResponse;
import com.pranit.workspace.shared.wrapper.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/features")
@RequiredArgsConstructor
@Tag(
        name = "Feature",
        description = "Endpoints for Project Feature Catalog APIs"
)
public class FeatureController {

    private final FeatureService featureService;

    @PostMapping(value = "/new", version = "v1")
    @Operation(summary = "Create feature", description = "Creates a new feature entry.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Feature created successfully"),
            @ApiResponse(responseCode = "400", description = "Validation failed for request body",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<String> createNewFeature(@Valid @RequestBody final CreateFeatureRequest request) {
        this.featureService.createNewFeature(request);
        return ResponseEntity.status(HttpStatus.CREATED).body("New Feature created.");
    }

    @GetMapping(version = "v1")
    @Operation(summary = "Get paginated features", description = "Retrieves a paginated list of features.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Features retrieved successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = PageResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid pagination parameters",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<PageResponse<FeatureResponse>> getAllFeatures(
            @PageableDefault(size = 20, sort = "name", direction = Sort.Direction.ASC)
            @ParameterObject final Pageable pageable) {
        return ResponseEntity.ok(this.featureService.getFeatures(pageable));
    }

    @GetMapping(value = "/{id}", version = "v1")
    @Operation(summary = "Get feature by ID", description = "Retrieves details of a specific feature.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Feature retrieved successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = FeatureResponse.class))),
            @ApiResponse(responseCode = "404", description = "Feature not found with provided ID",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<FeatureResponse> getFeatureById(
            @Parameter(description = "Unique ID of the feature", required = true)
            @PathVariable final Long id) {
        return ResponseEntity.ok(this.featureService.getFeatureById(id));
    }

    @PatchMapping(value = "/{id}", version = "v1")
    @Operation(summary = "Update feature", description = "Updates an existing feature entry.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Feature updated successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = FeatureResponse.class))),
            @ApiResponse(responseCode = "400", description = "Validation failed for request body",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Feature not found with provided ID",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<FeatureResponse> updateFeature(
            @Parameter(description = "Unique ID of the feature", required = true)
            @PathVariable final Long id,
            @Valid @RequestBody final UpdateFeatureRequest request) {
        return ResponseEntity.ok(this.featureService.updateFeature(id, request));
    }

    @DeleteMapping(value = "/{id}", version = "v1")
    @Operation(summary = "Delete feature", description = "Permanently removes a feature entry.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Feature deleted successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Feature not found with provided ID",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Void> deleteFeature(
            @Parameter(description = "Unique ID of the feature to delete", required = true)
            @PathVariable final Long id) {
        this.featureService.deleteFeature(id);
        return ResponseEntity.noContent().build();
    }
}
