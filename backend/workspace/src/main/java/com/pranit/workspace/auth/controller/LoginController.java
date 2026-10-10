package com.pranit.workspace.auth.controller;

import com.pranit.workspace.auth.application.service.LoginService;
import com.pranit.workspace.auth.application.service.LogoutService;
import com.pranit.workspace.auth.application.service.TokenService;
import com.pranit.workspace.auth.domain.dto.LoginRequest;
import com.pranit.workspace.auth.domain.dto.LoginResponse;
import com.pranit.workspace.auth.domain.dto.MfaTokenRequest;
import com.pranit.workspace.auth.domain.dto.UserResponse;
import com.pranit.workspace.auth.exception.UnauthorizedException;
import com.pranit.workspace.shared.wrapper.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Validated
@Tag(
        name = "Authentication",
        description = "Endpoints for user authentication, MFA verification, token refresh, and session logout"
)
public class LoginController {

    private final LoginService loginService;
    private final TokenService tokenService;
    private final LogoutService logoutService;

    @PostMapping(value = "/login", version = "v1")
    @Operation(summary = "Authenticate user credentials", description = "Authenticates user credentials and returns JWT session tokens or prompts for MFA one-time token.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User authenticated successfully or MFA challenge initiated",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = LoginResponse.class))),
            @ApiResponse(responseCode = "400", description = "Validation failed for request body",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Invalid credentials or unauthorized account status",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody final LoginRequest request, final HttpServletRequest httpRequest) {
        return ResponseEntity.status(HttpStatus.OK).body(this.loginService.authenticateUser(request, httpRequest));
    }

    @PostMapping(value = "/mfa/ott", version = "v1")
    @Operation(summary = "Verify multi-factor authentication token", description = "Verifies one-time MFA authentication token and returns authenticated session tokens.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "MFA verified successfully and session tokens issued",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = LoginResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid or expired MFA token",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "MFA verification failed",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<LoginResponse> verifyMfa(
            @Valid @RequestBody final MfaTokenRequest request,
            final HttpServletRequest httpRequest,
            final HttpServletResponse response) {
        return ResponseEntity.status(HttpStatus.OK).body(this.loginService.verifyMfa(request, httpRequest, response));
    }

    @GetMapping(value = "/me", version = "v1")
    @Operation(summary = "Get current authenticated user details", description = "Retrieves profile details and security authorities for the authenticated user session.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Authenticated user retrieved successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<UserResponse> me() {
        return ResponseEntity.status(HttpStatus.OK).body(this.loginService.getCurrentUser());
    }

    @PostMapping(value = "/refresh", version = "v1")
    @Operation(summary = "Refresh authentication tokens", description = "Issues a refreshed JWT access token using a valid HTTP-only refresh token cookie or header.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Access token refreshed successfully",
                    content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string", example = "Token refreshed successfully"))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Refresh token missing, expired, or revoked",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<String> refreshToken(final HttpServletRequest request, final HttpServletResponse response) {
        final String refreshToken = this.tokenService.readRefreshTokenFromRequest(request)
                .orElseThrow(() -> {
                    log.warn("No refresh token found in request");
                    return new UnauthorizedException("No refresh token found");
                });
        this.tokenService.generateNewRefreshToken(refreshToken, response);
        return ResponseEntity.status(HttpStatus.OK).body("Token refreshed successfully");
    }

    @PostMapping(value = "/logout", version = "v1")
    @Operation(summary = "Logout authenticated session", description = "Revokes the active refresh token and clears authentication cookies.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Session terminated and user logged out successfully")
    })
    public ResponseEntity<String> logout(final HttpServletRequest request, final HttpServletResponse response) {
        this.logoutService.readRefreshTokenFromRequest(request)
                .ifPresent(this.logoutService::revokedRefreshToken);
        this.logoutService.clearResponse(response);
        log.info("User logged out successfully");
        return ResponseEntity.ok("Logout successful");
    }
}