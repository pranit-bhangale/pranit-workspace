package com.pranit.workspace.portfolio.controller;

import com.pranit.workspace.portfolio.application.service.ContactMessageService;
import com.pranit.workspace.portfolio.domain.dto.ContactMessageRequest;
import com.pranit.workspace.portfolio.domain.dto.ContactMessageResponse;
import com.pranit.workspace.portfolio.domain.dto.ContactStatusUpdateRequest;
import com.pranit.workspace.portfolio.domain.enums.MessageStatus;
import com.pranit.workspace.shared.wrapper.ErrorResponse;
import com.pranit.workspace.shared.wrapper.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
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

@RestController
@RequestMapping("/api/contact")
@RequiredArgsConstructor
@Tag(
        name = "Contact",
        description = "Endpoints for visitor inquiries, contact messages, and admin message triage"
)
public class ContactMessageController {

    private final ContactMessageService contactMessageService;

    @PostMapping(version = "v1")
    @Operation(summary = "Submit a contact message (Public)", description = "Public endpoint allowing website visitors to submit contact inquiries and messages.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Contact message submitted successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ContactMessageResponse.class))),
            @ApiResponse(responseCode = "400", description = "Validation failed for request body",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<ContactMessageResponse> submitMessage(
            @Valid @RequestBody final ContactMessageRequest request,
            final HttpServletRequest servletRequest) {
        final String ipAddress = resolveClientIp(servletRequest);
        final String userAgent = servletRequest.getHeader("User-Agent");
        final ContactMessageResponse response = this.contactMessageService.submitMessage(request, ipAddress, userAgent);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    private String resolveClientIp(final HttpServletRequest request) {
        final String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isBlank())
            return xForwardedFor.split(",")[0].trim();
        return request.getRemoteAddr();
    }

    @GetMapping(value = "/messages", version = "v1")
    @Operation(summary = "Get paginated contact messages", description = "Retrieves a paginated list of visitor messages. Requires Admin role.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Contact messages retrieved successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = PageResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden - Admin role required",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<PageResponse<ContactMessageResponse>> getAllMessages(
            @Parameter(description = "Zero-based page index") @RequestParam(name = "page", defaultValue = "0") @Min(0) int page,
            @Parameter(description = "Number of items per page") @RequestParam(name = "size", defaultValue = "5") @Min(1) @Max(5) int size,
            @Parameter(description = "Optional keyword to filter contact messages") @RequestParam(name = "keyword", required = false) String keyword,
            @Parameter(description = "Filter by message status") @RequestParam(name = "status", required = false) final MessageStatus status,
            @Parameter(description = "Property field to sort by") @RequestParam(name = "sortBy", defaultValue = "createdAt") String sortBy,
            @Parameter(description = "Sort direction: ASC or DESC") @RequestParam(name = "sortDirection", defaultValue = "ASC") String sortDirection
    ) {
        return ResponseEntity.ok(this.contactMessageService.getAllMessages(page, size, keyword, status, sortBy, sortDirection));
    }

    @GetMapping(value = "/messages/{id}", version = "v1")
    @Operation(summary = "Get contact message by ID", description = "Retrieves full details of a specific contact message including sender metadata. Requires Admin role.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Contact message retrieved successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ContactMessageResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden - Admin role required",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Contact message not found with provided ID",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<ContactMessageResponse> getMessageById(
            @Parameter(description = "Unique ID of the contact message", required = true)
            @PathVariable final Long id) {
        return ResponseEntity.ok(contactMessageService.getMessageById(id));
    }

    @PatchMapping(value = "/messages/{id}/status", version = "v1")
    @Operation(summary = "Update contact message status", description = "Updates message status (e.g. mark as READ, REPLIED, or ARCHIVED). Requires Admin role.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Message status updated successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ContactMessageResponse.class))),
            @ApiResponse(responseCode = "400", description = "Validation failed for request body",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden - Admin role required",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Contact message not found with provided ID",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<ContactMessageResponse> updateMessageStatus(
            @Parameter(description = "Unique ID of the contact message", required = true)
            @PathVariable final Long id,
            @Valid @RequestBody final ContactStatusUpdateRequest request) {
        return ResponseEntity.ok(contactMessageService.updateMessageStatus(id, request));
    }

    @DeleteMapping(value = "/messages/{id}", version = "v1")
    @Operation(summary = "Delete a contact message", description = "Permanently removes a contact message record. Requires Admin role.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Contact message deleted successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden - Admin role required",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Contact message not found with provided ID",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Void> deleteMessage(
            @Parameter(description = "Unique ID of the contact message to delete", required = true)
            @PathVariable final Long id) {
        contactMessageService.deleteMessage(id);
        return ResponseEntity.noContent().build();
    }
}
