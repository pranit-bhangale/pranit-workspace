package com.pranit.workspace.auth.controller;

import com.pranit.workspace.auth.application.service.UserService;
import com.pranit.workspace.auth.domain.dto.ProfileMediaResponse;
import com.pranit.workspace.auth.domain.dto.UserUpdateRequest;
import com.pranit.workspace.auth.domain.dto.UserUpdateResponse;
import com.pranit.workspace.shared.wrapper.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Validated
@Tag(
        name = "Profile Management",
        description = "Endpoints for requesting profile information updpate."
)
public class UserController {

    private final UserService userService;

    @PatchMapping(value = "/me", version = "v1")
    @Operation(summary = "Update current user settings", description = "Updates editable settings for current user.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User settings updated successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = UserUpdateResponse.class))),
            @ApiResponse(responseCode = "400", description = "Validation failed or unauthorized privilege modification attempt",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "User account not found",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<UserUpdateResponse> updateCurrentUserProfile
            (@Valid @RequestBody final UserUpdateRequest request) {
        return ResponseEntity.ok(this.userService.updateCurrentUser(request));
    }

    @DeleteMapping(value = "/me", version = "v1")
    @Operation(summary = "Deactivate current user account", description = "Deactivates the currently authenticated user's account.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "User account deactivated successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "User account not found",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Void> deleteCurrentUser() {
        userService.deleteCurrentUser();
        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/me/media", version = "v1", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload current user's avatar and resume", description = "Uploads profile media to MinIO and updates the current user's profile.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Profile media updated"),
            @ApiResponse(responseCode = "400", description = "Invalid file"),
            @ApiResponse(responseCode = "401", description = "Authentication required")
    })
    public ResponseEntity<ProfileMediaResponse> updateProfileMedia
            (@RequestPart(value = "avatar", required = false) MultipartFile avatar,
             @RequestPart(value = "resume", required = false) MultipartFile resume) {
        return ResponseEntity.ok(this.userService.uploadProfileMedia(avatar, resume));
    }

    @GetMapping(value = "/me/media", version = "v1")
    @Operation(summary = "Get current user's profile media", description = "Retrieves the current user's avatar and resume details.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Profile media retrieved"),
            @ApiResponse(responseCode = "401", description = "Authentication required")
    })
    public ResponseEntity<ProfileMediaResponse> getProfileMedia() {
        return ResponseEntity.ok(this.userService.getProfileMedia());
    }
}
