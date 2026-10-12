package com.pranit.workspace.portfolio.controller;

import com.pranit.workspace.portfolio.application.service.ExperienceService;
import com.pranit.workspace.portfolio.domain.dto.CreateTechRequest;
import com.pranit.workspace.portfolio.domain.dto.TechStackResponse;
import com.pranit.workspace.portfolio.domain.dto.UpdateTechRequest;
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
@RequestMapping("/api/tech-stacks")
@RequiredArgsConstructor
@Tag(
        name = "Tech Stack",
        description = "Endpoints for technologies, programming languages, libraries, and tech stack definitions"
)
public class TechStackController {

    private final ExperienceService experienceService;

    @PostMapping(value = "/new", version = "v1")
    @Operation(summary = "Create tech stack", description = "Creates a new tech stack entry.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Tech stack created successfully"),
            @ApiResponse(responseCode = "400", description = "Validation failed for request body",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<String> createNewTechStack(@Valid @RequestBody final CreateTechRequest request) {
        this.experienceService.createNewTechStack(request);
        return ResponseEntity.status(HttpStatus.CREATED).body("New Tech Stack created.");
    }

    @GetMapping(version = "v1")
    @Operation(summary = "Get paginated tech stacks", description = "Retrieves a paginated list of tech stacks.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tech stacks retrieved successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = PageResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid pagination parameters",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<PageResponse<TechStackResponse>> getAllTechStacks(
            @PageableDefault(size = 20, sort = "name", direction = Sort.Direction.ASC)
            @ParameterObject final Pageable pageable) {
        return ResponseEntity.ok(this.experienceService.getAllTechStacks(pageable));
    }

    @GetMapping(value = "/{id}", version = "v1")
    @Operation(summary = "Get tech stack by ID", description = "Retrieves details of a specific tech stack.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tech stack retrieved successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = TechStackResponse.class))),
            @ApiResponse(responseCode = "404", description = "Tech stack not found with provided ID",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<TechStackResponse> getTechStackById(
            @Parameter(description = "Unique ID of the tech stack", required = true)
            @PathVariable final Long id) {
        return ResponseEntity.ok(this.experienceService.getTechStackById(id));
    }

    @PatchMapping(value = "/{id}", version = "v1")
    @Operation(summary = "Update tech stack", description = "Updates an existing tech stack entry.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tech stack updated successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = TechStackResponse.class))),
            @ApiResponse(responseCode = "400", description = "Validation failed for request body",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Tech stack not found with provided ID",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<TechStackResponse> updateTechStack(
            @Parameter(description = "Unique ID of the tech stack", required = true)
            @PathVariable final Long id,
            @Valid @RequestBody final UpdateTechRequest request) {
        return ResponseEntity.ok(this.experienceService.updateTechStack(id, request));
    }

    @DeleteMapping(value = "/{id}", version = "v1")
    @Operation(summary = "Delete tech stack", description = "Permanently removes a tech stack entry.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Tech stack deleted successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Tech stack not found with provided ID",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Void> deleteTechStack(
            @Parameter(description = "Unique ID of the tech stack to delete", required = true)
            @PathVariable final Long id) {
        this.experienceService.deleteTechStackById(id);
        return ResponseEntity.noContent().build();
    }
}
