package com.pranit.workspace.portfolio.controller;

import com.pranit.workspace.portfolio.application.service.ProjectService;
import com.pranit.workspace.portfolio.domain.dto.CreateProjectRequest;
import com.pranit.workspace.portfolio.domain.dto.ProjectResponse;
import com.pranit.workspace.portfolio.domain.dto.UpdateProjectRequest;
import com.pranit.workspace.portfolio.domain.enums.ProjectCategory;
import com.pranit.workspace.portfolio.domain.enums.ProjectStatus;
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
@RequestMapping("/api/projects")
@RequiredArgsConstructor
@Tag(
        name = "Project",
        description = "Endpoints for portfolio projects, showcase applications, features, and tech stacks"
)
public class ProjectController {

    private final ProjectService projectService;

    @GetMapping(value = "/categories", version = "v1")
    @Operation(summary = "Get project categories", description = "Retrieves all available project classifications (e.g. FULL_STACK, FRONTEND, BACKEND, MOBILE, AI_ML, DEVOPS).")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Project categories retrieved successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, array = @ArraySchema(schema = @Schema(implementation = ProjectCategory.class))))
    })
    public ResponseEntity<List<ProjectCategory>> getCategories() {
        return ResponseEntity.ok(this.projectService.getCategories());
    }

    @GetMapping(value = "/statuses", version = "v1")
    @Operation(summary = "Get project statuses", description = "Retrieves all available project lifecycle statuses (e.g. PLANNING, IN_DEVELOPMENT, COMPLETED, ARCHIVED).")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Project statuses retrieved successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, array = @ArraySchema(schema = @Schema(implementation = ProjectStatus.class))))
    })
    public ResponseEntity<List<ProjectStatus>> getStatuses() {
        return ResponseEntity.ok(this.projectService.getStatuses());
    }

    @PostMapping(value = "/new", version = "v1")
    @Operation(summary = "Create new project", description = "Creates a new showcase portfolio project.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Project created successfully"),
            @ApiResponse(responseCode = "400", description = "Validation failed for request body",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Project with title already exists",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<String> createNewProject(@Valid @RequestBody final CreateProjectRequest request) {
        this.projectService.createNewProject(request);
        return ResponseEntity.status(HttpStatus.CREATED).body("New Project created.");
    }

    @GetMapping(version = "v1")
    @Operation(summary = "Get paginated projects", description = "Retrieves a paginated list of projects with optional keyword, category, status, and featured filters.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Projects retrieved successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = PageResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid pagination or query parameters",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<PageResponse<ProjectResponse>> getProjects(
            @Parameter(description = "Zero-based page index") @RequestParam(name = "page", defaultValue = "0") @Min(0) int page,
            @Parameter(description = "Number of items per page") @RequestParam(name = "size", defaultValue = "10") @Min(1) @Max(25) int size,
            @Parameter(description = "Keyword to filter by title or description") @RequestParam(name = "keyword", required = false) String keyword,
            @Parameter(description = "Filter by project category") @RequestParam(name = "category", required = false) ProjectCategory category,
            @Parameter(description = "Filter by project status") @RequestParam(name = "status", required = false) ProjectStatus status,
            @Parameter(description = "Filter by featured flag") @RequestParam(name = "isFeatured", required = false) Boolean isFeatured,
            @Parameter(description = "Property field to sort by") @RequestParam(name = "sortBy", defaultValue = "displayOrder") String sortBy,
            @Parameter(description = "Sort direction: ASC or DESC") @RequestParam(name = "sortDirection", defaultValue = "ASC") String sortDirection
    ) {
        return ResponseEntity.ok(this.projectService.getProjects(page, size, keyword, category, status, isFeatured, sortBy, sortDirection));
    }

    @GetMapping(value = "/{id}", version = "v1")
    @Operation(summary = "Get project by ID", description = "Retrieves full details of a specific project including features, tech stacks, and tags.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Project retrieved successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProjectResponse.class))),
            @ApiResponse(responseCode = "404", description = "Project not found with provided ID",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<ProjectResponse> getProjectById(
            @Parameter(description = "Unique ID of the project", required = true)
            @PathVariable final Long id) {
        return ResponseEntity.ok(this.projectService.getProjectById(id));
    }

    @PatchMapping(value = "/{id}", version = "v1")
    @Operation(summary = "Update project", description = "Updates details of an existing portfolio project.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Project updated successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProjectResponse.class))),
            @ApiResponse(responseCode = "400", description = "Validation failed for request body",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Project not found with provided ID",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Project with duplicate title already exists",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<ProjectResponse> updateProject(
            @Parameter(description = "Unique ID of the project", required = true)
            @PathVariable final Long id,
            @Valid @RequestBody final UpdateProjectRequest request) {
        return ResponseEntity.ok(this.projectService.updateProject(id, request));
    }

    @DeleteMapping(value = "/{id}", version = "v1")
    @Operation(summary = "Delete project", description = "Permanently removes a project record.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Project deleted successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Project not found with provided ID",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Void> deleteProject(
            @Parameter(description = "Unique ID of the project to delete", required = true)
            @PathVariable final Long id) {
        this.projectService.deleteProject(id);
        return ResponseEntity.noContent().build();
    }
}
