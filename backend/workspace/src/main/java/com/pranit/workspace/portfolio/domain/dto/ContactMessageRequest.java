package com.pranit.workspace.portfolio.domain.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record ContactMessageRequest(
        @NotBlank(message = "Sender name is required")
        @Size(max = 100, message = "Sender name cannot exceed 100 characters")
        String senderName,

        @NotBlank(message = "Sender email is required")
        @Email(message = "Invalid email format")
        @Size(max = 100, message = "Sender email cannot exceed 100 characters")
        String senderEmail,

        @Size(max = 30, message = "Sender phone cannot exceed 30 characters")
        String senderPhone,

        @NotBlank(message = "Message content is required")
        @Size(max = 5000, message = "Message content cannot exceed 5000 characters")
        String messageContent
) {
}
