package com.pranit.workspace.portfolio.controller;

import com.pranit.workspace.portfolio.application.service.EducationService;
import com.pranit.workspace.portfolio.domain.dto.CreateEducationRequest;
import com.pranit.workspace.portfolio.domain.dto.EducationDetailResponse;
import com.pranit.workspace.portfolio.domain.dto.UpdateEducationRequest;
import com.pranit.workspace.portfolio.domain.enums.DegreeType;
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
@RequestMapping("/api/education")
@RequiredArgsConstructor
@Tag(
        name = "Education Details",
        description = "Endpoints for academic history, degree credentials, and educational qualifications"
)
public class EducationController {

    private final EducationService educationService;

    @GetMapping(value = "/degree", version = "v1")
    @Operation(summary = "Get degree types", description = "Retrieves all supported academic degree classifications (e.g. BACHELOR, MASTER, DIPLOMA, CERTIFICATE).")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Degree types retrieved successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, array = @ArraySchema(schema = @Schema(implementation = DegreeType.class))))
    })
    public ResponseEntity<List<DegreeType>> getDegrees() {
        return ResponseEntity.ok(this.educationService.getDegrees());
    }

    @PostMapping(value = "/details/new", version = "v1")
    @Operation(summary = "Create education detail", description = "Creates a new formal education or qualification entry.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Education qualification created successfully"),
            @ApiResponse(responseCode = "400", description = "Validation failed for request body",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Education record with degree title already exists",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<String> createNewEducationDetails(@Valid @RequestBody final CreateEducationRequest request) {
        this.educationService.createNewEducationDetail(request);
        return ResponseEntity.status(HttpStatus.CREATED).body("New education detail created.");
    }

    @GetMapping(value = "/details", version = "v1")
    @Operation(summary = "Get paginated education details", description = "Retrieves a paginated list of academic qualifications.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Education details retrieved successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = PageResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid pagination query parameters",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<PageResponse<EducationDetailResponse>> getAllDetails(
            @Parameter(description = "Zero-based page index") @RequestParam(name = "page", defaultValue = "0") @Min(0) int page,
            @Parameter(description = "Number of items per page") @RequestParam(name = "size", defaultValue = "5") @Min(1) @Max(25) int size,
            @Parameter(description = "Optional keyword to filter") @RequestParam(name = "keyword", required = false) String keyword,
            @Parameter(description = "Property field to sort by") @RequestParam(name = "sortBy", defaultValue = "displayOrder") String sortBy,
            @Parameter(description = "Sort direction: ASC or DESC") @RequestParam(name = "sortDirection", defaultValue = "ASC") String sortDirection
    ) {
        return ResponseEntity.ok(this.educationService.getEducationDetails(page, size, keyword, sortBy, sortDirection));
    }

    @GetMapping(value = "/detail/{id}", version = "v1")
    @Operation(summary = "Get education detail by ID", description = "Retrieves full academic details of a specific qualification.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Education detail retrieved successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = EducationDetailResponse.class))),
            @ApiResponse(responseCode = "404", description = "Education detail not found with provided ID",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<EducationDetailResponse> getEducationDetailById(
            @Parameter(description = "Unique ID of the education detail", required = true)
            @PathVariable final Long id) {
        return ResponseEntity.ok(this.educationService.getEducationDetailById(id));
    }

    @PatchMapping(value = "/detail/{id}", version = "v1")
    @Operation(summary = "Update education detail", description = "Updates details of an existing academic credential.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Education detail updated successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = EducationDetailResponse.class))),
            @ApiResponse(responseCode = "400", description = "Validation failed for request body",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Education detail not found with provided ID",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Education detail with duplicate degree title already exists",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<EducationDetailResponse> updateEducationDetails(
            @Parameter(description = "Unique ID of the education detail", required = true)
            @PathVariable final Long id,
            @Valid @RequestBody final UpdateEducationRequest request) {
        return ResponseEntity.ok(this.educationService.updateEducationDetails(id, request));
    }

    @DeleteMapping(value = "/{id}", version = "v1")
    @Operation(summary = "Delete education detail", description = "Permanently removes an academic qualification record.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Education detail deleted successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Education detail not found with provided ID",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Void> deleteEducationDetail(
            @Parameter(description = "Unique ID of the detail to delete", required = true)
            @PathVariable final Long id) {
        this.educationService.deleteEducationDetail(id);
        return ResponseEntity.noContent().build();
    }
}
