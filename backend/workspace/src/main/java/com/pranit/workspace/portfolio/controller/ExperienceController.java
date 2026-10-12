package com.pranit.workspace.portfolio.controller;

import com.pranit.workspace.portfolio.application.service.ExperienceService;
import com.pranit.workspace.portfolio.domain.dto.CreateExperienceRequest;
import com.pranit.workspace.portfolio.domain.dto.ExperienceResponse;
import com.pranit.workspace.portfolio.domain.dto.UpdateExperienceRequest;
import com.pranit.workspace.portfolio.domain.enums.EmploymentType;
import com.pranit.workspace.portfolio.domain.enums.WorkplaceType;
import com.pranit.workspace.shared.wrapper.ErrorResponse;
import com.pranit.workspace.shared.wrapper.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/experiences")
@RequiredArgsConstructor
@Tag(
        name = "Experience",
        description = "Endpoints for professional career history, roles, companies, and work experience"
)
public class ExperienceController {

    private final ExperienceService experienceService;

    @GetMapping(value = "/workplace-types", version = "v1")
    @Operation(summary = "Get workplace types", description = "Retrieves all supported workplace types (e.g. REMOTE, ON_SITE, HYBRID).")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Workplace types retrieved successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, array = @ArraySchema(schema = @Schema(implementation = WorkplaceType.class))))
    })
    public ResponseEntity<List<WorkplaceType>> getWorkplaceTypes() {
        return ResponseEntity.ok(this.experienceService.getWorkplaceTypes());
    }

    @GetMapping(value = "/employment-types", version = "v1")
    @Operation(summary = "Get employment types", description = "Retrieves all supported employment contract types (e.g. FULL_TIME, PART_TIME, CONTRACT, INTERNSHIP).")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Employment types retrieved successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, array = @ArraySchema(schema = @Schema(implementation = EmploymentType.class))))
    })
    public ResponseEntity<List<EmploymentType>> getEmploymentTypes() {
        return ResponseEntity.ok(this.experienceService.getEmploymentTypes());
    }

    @PostMapping(value = "/new", version = "v1")
    @Operation(summary = "Create new experience", description = "Creates a new professional work experience record.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Experience created successfully"),
            @ApiResponse(responseCode = "400", description = "Validation failed for request body",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<String> createNewExperience(@Valid @RequestBody final CreateExperienceRequest request) {
        this.experienceService.createNewExperience(request);
        return ResponseEntity.status(HttpStatus.CREATED).body("New Experience created.");
    }

    @GetMapping(version = "v1")
    @Operation(summary = "Get paginated experiences", description = "Retrieves a paginated list of work experiences.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Experiences retrieved successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = PageResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid pagination query parameters",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<PageResponse<ExperienceResponse>> getAllExperiences(
            @Parameter(description = "Zero-based page index") @RequestParam(name = "page", defaultValue = "0") @Min(0) int page,
            @Parameter(description = "Number of items per page") @RequestParam(name = "size", defaultValue = "5") @Min(1) @Max(25) int size,
            @Parameter(description = "Optional keyword to filter") @RequestParam(name = "keyword", required = false) String keyword,
            @Parameter(description = "Property field to sort by") @RequestParam(name = "sortBy", defaultValue = "displayOrder") String sortBy,
            @Parameter(description = "Sort direction: ASC or DESC") @RequestParam(name = "sortDirection", defaultValue = "ASC") String sortDirection
    ) {
        return ResponseEntity.ok(this.experienceService.getAllExperiences(page, size, keyword, sortBy, sortDirection));
    }

    @GetMapping(value = "/{id}", version = "v1")
    @Operation(summary = "Get experience by ID", description = "Retrieves full details of a specific work experience.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Experience retrieved successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ExperienceResponse.class))),
            @ApiResponse(responseCode = "404", description = "Experience not found with provided ID",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<ExperienceResponse> getExperienceById(
            @Parameter(description = "Unique ID of the experience", required = true)
            @PathVariable final Long id) {
        return ResponseEntity.ok(this.experienceService.getExperienceById(id));
    }

    @PatchMapping(value = "/{id}", version = "v1")
    @Operation(summary = "Update experience", description = "Updates details of an existing experience record.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Experience updated successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ExperienceResponse.class))),
            @ApiResponse(responseCode = "400", description = "Validation failed for request body",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Experience not found with provided ID",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<ExperienceResponse> updateExperience(
            @Parameter(description = "Unique ID of the experience", required = true)
            @PathVariable final Long id,
            @Valid @RequestBody final UpdateExperienceRequest request) {
        return ResponseEntity.ok(this.experienceService.updateExperienceById(id, request));
    }

    @DeleteMapping(value = "/{id}", version = "v1")
    @Operation(summary = "Delete experience", description = "Permanently removes an experience record.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Experience deleted successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Experience not found with provided ID",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Void> deleteExperience(
            @Parameter(description = "Unique ID of the experience to delete", required = true)
            @PathVariable final Long id) {
        this.experienceService.deleteExperienceById(id);
        return ResponseEntity.noContent().build();
    }
}
