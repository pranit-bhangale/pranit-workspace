package com.pranit.workspace.auth.controller;

import com.pranit.workspace.auth.application.service.UserService;
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
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
