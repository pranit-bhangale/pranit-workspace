package com.pranit.workspace.portfolio.domain.dto;

import com.pranit.workspace.portfolio.domain.entity.ContactMessage;
import com.pranit.workspace.portfolio.domain.entity.ContactSender;
import com.pranit.workspace.portfolio.domain.enums.MessageStatus;
import lombok.Builder;

import java.time.Instant;

@Builder
public record ContactMessageResponse(
        Long id,
        ContactSenderResponse sender,
        String messageContent,
        MessageStatus status,
        Instant createdAt
) {
    
    public static ContactMessageResponse from(final ContactMessage message) {
        final ContactSender sender = message.getSender();
        final ContactSenderResponse senderResponse = sender != null
                ? ContactSenderResponse.builder()
                .name(sender.getName())
                .email(sender.getEmail())
                .phone(sender.getPhone())
                .build()
                : null;

        return ContactMessageResponse.builder()
                .id(message.getId())
                .sender(senderResponse)
                .messageContent(message.getMessageContent())
                .status(message.getStatus())
                .createdAt(message.getCreatedAt())
                .build();
    }

    @Builder
    public record ContactSenderResponse(
            String name,
            String email,
            String phone
    ) {
    }
}
