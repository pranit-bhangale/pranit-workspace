package com.pranit.workspace.portfolio.domain.dto;

import com.pranit.workspace.portfolio.domain.entity.ContactMessage;
import com.pranit.workspace.portfolio.domain.entity.ContactSender;
import com.pranit.workspace.portfolio.domain.enums.MessageStatus;
import lombok.Builder;

import java.time.Instant;

@Builder
public record ContactMessageResponse(
        Long id,
        ContactSender sender,
        String messageContent,
        MessageStatus status,
        Instant createdAt
) {
    public static ContactMessageResponse from(final ContactMessage message) {
        return ContactMessageResponse.builder()
                .id(message.getId())
                .sender(message.getSender())
                .messageContent(message.getMessageContent())
                .status(message.getStatus())
                .createdAt(message.getCreatedAt())
                .build();
    }
}
