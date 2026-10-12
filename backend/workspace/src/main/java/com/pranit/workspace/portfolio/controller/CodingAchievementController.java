package com.pranit.workspace.portfolio.controller;

import com.pranit.workspace.portfolio.application.service.CodingAchievementService;
import com.pranit.workspace.portfolio.domain.dto.CodingAchievementResponse;
import com.pranit.workspace.portfolio.domain.dto.CreateCodingAchievementRequest;
import com.pranit.workspace.portfolio.domain.dto.UpdateCodingAchievementRequest;
import com.pranit.workspace.portfolio.domain.enums.CodingPlatform;
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

import java.util.List;

@RestController
@RequestMapping("/api/achievements")
@RequiredArgsConstructor
@Tag(
        name = "Coding Achievements",
        description = "Endpoints for competitive programming profiles and coding platform achievements"
)
public class CodingAchievementController {

    private final CodingAchievementService codingAchievementService;

    @GetMapping(value = "/platforms", version = "v1")
    @Operation(summary = "Get coding platforms", description = "Retrieves supported competitive programming and coding platforms (e.g. LEETCODE, GEEKS_FOR_GEEKS, CODECHEF).")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Coding platforms retrieved successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, array = @ArraySchema(schema = @Schema(implementation = CodingPlatform.class))))
    })
    public ResponseEntity<List<CodingPlatform>> getCodingPlatforms() {
        return ResponseEntity.ok(this.codingAchievementService.getCodingPlatforms());
    }

    @PostMapping(value = "/new", version = "v1")
    @Operation(summary = "Create coding achievement", description = "Creates a new coding platform milestone record.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Coding achievement created successfully"),
            @ApiResponse(responseCode = "400", description = "Validation failed for request body",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<String> createNewAchievement(@Valid @RequestBody final CreateCodingAchievementRequest request) {
        this.codingAchievementService.createNewCodingAchievement(request);
        return ResponseEntity.status(HttpStatus.CREATED).body("New achievement created.");
    }

    @GetMapping(version = "v1")
    @Operation(summary = "Get paginated coding achievements", description = "Retrieves a paginated list of coding platform milestones ordered by display order.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Coding achievements retrieved successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = PageResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid pagination parameters",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<PageResponse<CodingAchievementResponse>> getCodingAchievements(
            @PageableDefault(size = 20, sort = "displayOrder", direction = Sort.Direction.ASC)
            @ParameterObject final Pageable pageable) {
        return ResponseEntity.ok(codingAchievementService.getCodingAchievements(pageable));
    }

    @GetMapping(value = "/{id}", version = "v1")
    @Operation(summary = "Get coding achievement by ID", description = "Retrieves details of a specific coding platform achievement record.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Achievement retrieved successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = CodingAchievementResponse.class))),
            @ApiResponse(responseCode = "404", description = "Coding achievement not found with provided ID",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<CodingAchievementResponse> getAchievementById(
            @Parameter(description = "Unique ID of the achievement", required = true)
            @PathVariable final Long id) {
        return ResponseEntity.ok(this.codingAchievementService.getAchievementById(id));
    }

    @PatchMapping(value = "/{id}", version = "v1")
    @Operation(summary = "Update coding achievement", description = "Updates details of an existing coding platform milestone.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Coding achievement updated successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = CodingAchievementResponse.class))),
            @ApiResponse(responseCode = "400", description = "Validation failed for request body",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Coding achievement not found with provided ID",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<CodingAchievementResponse> updateAchievement(
            @Parameter(description = "Unique ID of the achievement", required = true)
            @PathVariable final Long id,
            @Valid @RequestBody final UpdateCodingAchievementRequest request) {
        return ResponseEntity.ok(this.codingAchievementService.updateCodingAchievements(id, request));
    }

    @DeleteMapping(value = "/{id}", version = "v1")
    @Operation(summary = "Delete coding achievement", description = "Permanently removes a coding platform milestone record.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Coding achievement deleted successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Coding achievement not found with provided ID",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Void> deleteAchievement(
            @Parameter(description = "Unique ID of the achievement to delete", required = true)
            @PathVariable final Long id) {
        this.codingAchievementService.deleteCodingAchievement(id);
        return ResponseEntity.noContent().build();
    }
}
