package com.pranit.workspace.auth.controller;

import com.pranit.workspace.auth.application.service.VerificationService;
import com.pranit.workspace.auth.domain.dto.VerificationResponse;
import com.pranit.workspace.auth.domain.dto.VerifyOtp;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth/verify")
@RequiredArgsConstructor
@Validated
@Tag(
        name = "Verification",
        description = "Endpoints for verifying one-time passwords for registration and password reset"
)
public class VerificationController {

    private final VerificationService verificationService;

    @PostMapping(value = "/registration", version = "v1")
    @Operation(summary = "Verify registration OTP", description = "Validates the one-time password dispatched during registration and activates the account.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Registration OTP verified successfully and account activated",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = VerificationResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid or expired OTP token",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Registration record not found for OTP",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<VerificationResponse> verifyRegistration(@Valid @RequestBody final VerifyOtp request) {
        return ResponseEntity.ok(this.verificationService.verifyRegistration(request));
    }

    @PostMapping(value = "/password-reset", version = "v1")
    @Operation(summary = "Verify password reset OTP", description = "Validates the one-time password issued during password recovery.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Password reset OTP verified successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = VerificationResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid or expired OTP token",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Password reset request not found for OTP",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<VerificationResponse> verifyPasswordResetOtp(@Valid @RequestBody final VerifyOtp request) {
        return ResponseEntity.ok(this.verificationService.verifyPasswordResetOtp(request));
    }
}
