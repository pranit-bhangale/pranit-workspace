package com.pranit.workspace.portfolio.controller;

import com.pranit.workspace.portfolio.application.service.CodingAchievementService;
import com.pranit.workspace.portfolio.domain.dto.CodingAchievementResponse;
import com.pranit.workspace.portfolio.domain.dto.CreateCodingAchievementRequest;
import com.pranit.workspace.portfolio.domain.dto.UpdateCodingAchievementRequest;
import com.pranit.workspace.portfolio.domain.enums.CodingPlatform;
import com.pranit.workspace.shared.wrapper.PageResponse;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
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
        description = "Endpoints for Coding Achievements APIs"
)
public class CodingAchievementController {

    private final CodingAchievementService codingAchievementService;

    @GetMapping(value = "/platforms", version = "v1")
    public ResponseEntity<List<CodingPlatform>> getCodingPlatforms() {
        return ResponseEntity.ok(this.codingAchievementService.getCodingPlatforms());
    }

    @PostMapping(value = "/new", version = "v1")
    public ResponseEntity<String> createNewAchievement(@Valid @RequestBody final CreateCodingAchievementRequest request) {
        this.codingAchievementService.createNewCodingAchievement(request);
        return ResponseEntity.status(HttpStatus.CREATED).body("New achievement created.");
    }

    @GetMapping
    public ResponseEntity<PageResponse<CodingAchievementResponse>> getCodingAchievements
            (@PageableDefault(size = 20, sort = "displayOrder", direction = Sort.Direction.ASC)
             @ParameterObject final Pageable pageable) {
        return ResponseEntity.ok(codingAchievementService.getCodingAchievements(pageable));
    }

    @GetMapping(value = "/{id}", version = "v1")
    public ResponseEntity<CodingAchievementResponse> getAchievementById
            (@Parameter(description = "Unique ID of the achievement", required = true)
             @PathVariable final Long id) {
        return ResponseEntity.ok(this.codingAchievementService.getAchievementById(id));
    }

    @PatchMapping(value = "/{id}", version = "v1")
    public ResponseEntity<CodingAchievementResponse> updateAchievement(
            @Parameter(description = "Unique ID of the achievement", required = true)
            @PathVariable final Long id,
            @Valid @RequestBody final UpdateCodingAchievementRequest request) {
        return ResponseEntity.ok(this.codingAchievementService.updateCodingAchievements(id, request));
    }

    @DeleteMapping(value = "/{id}", version = "v1")
    public ResponseEntity<Void> deleteAchievement
            (@Parameter(description = "Unique ID of the achievement to delete", required = true)
             @PathVariable final Long id) {
        this.codingAchievementService.deleteCodingAchievement(id);
        return ResponseEntity.noContent().build();
    }
}
