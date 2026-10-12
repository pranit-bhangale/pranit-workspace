package com.pranit.workspace.portfolio.controller;

import com.pranit.workspace.portfolio.application.service.TagService;
import com.pranit.workspace.portfolio.domain.dto.CreateTagRequest;
import com.pranit.workspace.portfolio.domain.dto.TagResponse;
import com.pranit.workspace.portfolio.domain.dto.UpdateTagRequest;
import com.pranit.workspace.portfolio.domain.enums.Color;
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
@RequestMapping("/api/tags")
@RequiredArgsConstructor
@Tag(
        name = "Tag",
        description = "Endpoints for Project Tag Catalog APIs"
)
public class TagController {

    private final TagService tagService;

    @GetMapping(value = "/colors", version = "v1")
    @Operation(summary = "Get available colors", description = "Retrieves all supported tag UI colors.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tag colors retrieved successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, array = @ArraySchema(schema = @Schema(implementation = Color.class))))
    })
    public ResponseEntity<List<Color>> getColors() {
        return ResponseEntity.ok(this.tagService.getColors());
    }

    @PostMapping(value = "/new", version = "v1")
    @Operation(summary = "Create tag", description = "Creates a new tag entry.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Tag created successfully"),
            @ApiResponse(responseCode = "400", description = "Validation failed for request body",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<String> createNewTag(@Valid @RequestBody final CreateTagRequest request) {
        this.tagService.createNewTag(request);
        return ResponseEntity.status(HttpStatus.CREATED).body("New Tag created.");
    }

    @GetMapping(version = "v1")
    @Operation(summary = "Get paginated tags", description = "Retrieves a paginated list of tags.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tags retrieved successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = PageResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid pagination parameters",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<PageResponse<TagResponse>> getAllTags(
            @PageableDefault(size = 20, sort = "name", direction = Sort.Direction.ASC)
            @ParameterObject final Pageable pageable) {
        return ResponseEntity.ok(this.tagService.getTags(pageable));
    }

    @GetMapping(value = "/{id}", version = "v1")
    @Operation(summary = "Get tag by ID", description = "Retrieves details of a specific tag.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tag retrieved successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = TagResponse.class))),
            @ApiResponse(responseCode = "404", description = "Tag not found with provided ID",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<TagResponse> getTagById(
            @Parameter(description = "Unique ID of the tag", required = true)
            @PathVariable final Long id) {
        return ResponseEntity.ok(this.tagService.getTagById(id));
    }

    @PatchMapping(value = "/{id}", version = "v1")
    @Operation(summary = "Update tag", description = "Updates an existing tag entry.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tag updated successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = TagResponse.class))),
            @ApiResponse(responseCode = "400", description = "Validation failed for request body",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Tag not found with provided ID",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<TagResponse> updateTag(
            @Parameter(description = "Unique ID of the tag", required = true)
            @PathVariable final Long id,
            @Valid @RequestBody final UpdateTagRequest request) {
        return ResponseEntity.ok(this.tagService.updateTag(id, request));
    }

    @DeleteMapping(value = "/{id}", version = "v1")
    @Operation(summary = "Delete tag", description = "Permanently removes a tag entry.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Tag deleted successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Tag not found with provided ID",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Void> deleteTag(
            @Parameter(description = "Unique ID of the tag to delete", required = true)
            @PathVariable final Long id) {
        this.tagService.deleteTag(id);
        return ResponseEntity.noContent().build();
    }
}
