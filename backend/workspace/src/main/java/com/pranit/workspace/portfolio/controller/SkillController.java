package com.pranit.workspace.portfolio.controller;

import com.pranit.workspace.portfolio.application.service.SkillService;
import com.pranit.workspace.portfolio.domain.dto.CreateSkillRequest;
import com.pranit.workspace.portfolio.domain.dto.SkillResponse;
import com.pranit.workspace.portfolio.domain.dto.UpdateSkillRequest;
import com.pranit.workspace.portfolio.domain.enums.ProficiencyLevel;
import com.pranit.workspace.portfolio.domain.enums.SkillCategory;
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
@RequestMapping("/api/skills")
@RequiredArgsConstructor
@Tag(
        name = "Skill",
        description = "Endpoints for managing technical proficiencies, frameworks, tools, and developer skills"
)
public class SkillController {

    private final SkillService skillService;

    @GetMapping(value = "/category", version = "v1")
    @Operation(summary = "Get skill categories", description = "Retrieves all supported technical skill categories (e.g. FRONTEND, BACKEND, DATABASE, DEVOPS, TOOLS).")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Skill categories retrieved successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, array = @ArraySchema(schema = @Schema(implementation = SkillCategory.class))))
    })
    public ResponseEntity<List<SkillCategory>> getAllSkillCategories() {
        return ResponseEntity.ok(this.skillService.getSkillCategories());
    }

    @GetMapping(value = "/level", version = "v1")
    @Operation(summary = "Get proficiency levels", description = "Retrieves all supported skill proficiency levels (e.g. BEGINNER, INTERMEDIATE, ADVANCED, EXPERT).")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Proficiency levels retrieved successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, array = @ArraySchema(schema = @Schema(implementation = ProficiencyLevel.class))))
    })
    public ResponseEntity<List<ProficiencyLevel>> getAllProficiencyLevel() {
        return ResponseEntity.ok(this.skillService.getAllProficiencyLevel());
    }

    @PostMapping(value = "/new", version = "v1")
    @Operation(summary = "Create skill", description = "Creates a new technical skill entry.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Skill created successfully"),
            @ApiResponse(responseCode = "400", description = "Validation failed for request body",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Skill with this name already exists",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<String> createNewSkill(@Valid @RequestBody final CreateSkillRequest request) {
        this.skillService.createNewSkill(request);
        return ResponseEntity.status(HttpStatus.CREATED).body("New Skill created.");
    }

    @GetMapping(version = "v1")
    @Operation(summary = "Get paginated skills", description = "Retrieves a paginated list of skills with optional keyword search.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Skills retrieved successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = PageResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid pagination parameters",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<PageResponse<SkillResponse>> getAllSkills(
            @Parameter(description = "Zero-based page index") @RequestParam(name = "page", defaultValue = "0") @Min(0) int page,
            @Parameter(description = "Number of items per page") @RequestParam(name = "size", defaultValue = "5") @Min(1) @Max(25) int size,
            @Parameter(description = "Optional keyword to filter") @RequestParam(name = "keyword", required = false) String keyword,
            @Parameter(description = "Property field to sort by") @RequestParam(name = "sortBy", defaultValue = "displayOrder") String sortBy,
            @Parameter(description = "Sort direction: ASC or DESC") @RequestParam(name = "sortDirection", defaultValue = "ASC") String sortDirection
    ) {
        return ResponseEntity.ok(this.skillService.getSkills(page, size, keyword, sortBy, sortDirection));
    }

    @GetMapping(value = "/{id}", version = "v1")
    @Operation(summary = "Get skill by ID", description = "Retrieves full details of a specific technical skill.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Skill retrieved successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = SkillResponse.class))),
            @ApiResponse(responseCode = "404", description = "Skill not found with provided ID",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<SkillResponse> getSkillById(
            @Parameter(description = "Unique ID of the Skill", required = true)
            @PathVariable final Long id) {
        return ResponseEntity.ok(this.skillService.getSkillById(id));
    }

    @PatchMapping(value = "/{id}", version = "v1")
    @Operation(summary = "Update skill", description = "Updates details of an existing technical skill.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Skill updated successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = SkillResponse.class))),
            @ApiResponse(responseCode = "400", description = "Validation failed for request body",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Skill not found with provided ID",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Skill with this name already exists",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<SkillResponse> updateSkill(
            @Parameter(description = "Unique ID of the skill", required = true)
            @PathVariable final Long id,
            @Valid @RequestBody final UpdateSkillRequest request) {
        return ResponseEntity.ok(this.skillService.updateSkill(id, request));
    }

    @DeleteMapping(value = "/{id}", version = "v1")
    @Operation(summary = "Delete skill", description = "Permanently removes a technical skill record.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Skill deleted successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Skill not found with provided ID",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Void> deleteSkill(
            @Parameter(description = "Unique ID of the skill to delete", required = true)
            @PathVariable final Long id) {
        this.skillService.deleteSkill(id);
        return ResponseEntity.noContent().build();
    }
}
