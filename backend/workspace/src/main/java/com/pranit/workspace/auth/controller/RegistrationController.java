package com.pranit.workspace.auth.controller;

import com.pranit.workspace.auth.application.service.RegistrationService;
import com.pranit.workspace.auth.domain.dto.SignupRequest;
import com.pranit.workspace.auth.domain.dto.SignupResponse;
import com.pranit.workspace.shared.wrapper.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth/register")
@RequiredArgsConstructor
@Validated
@Tag(
        name = "Registration",
        description = "Endpoints for new administrator account registration and onboarding"
)
public class RegistrationController {

    private final RegistrationService registrationService;

    @PostMapping(version = "v1")
    @Operation(summary = "Register new administrator account", description = "Registers initial administrator credentials and sends an OTP for account activation.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Administrator registered successfully and verification OTP dispatched",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = SignupResponse.class))),
            @ApiResponse(responseCode = "400", description = "Validation failed for registration payload",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Account conflict - Username or email already registered",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<SignupResponse> registerNewAdmin(@Valid @RequestBody final SignupRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.registrationService.createNewAdminAccount(request));
    }
}
