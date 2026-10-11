package com.pranit.workspace.portfolio.application.service;

import com.pranit.workspace.portfolio.domain.dto.ContactMessageRequest;
import com.pranit.workspace.portfolio.domain.dto.ContactMessageResponse;
import com.pranit.workspace.portfolio.domain.dto.ContactStatusUpdateRequest;
import com.pranit.workspace.portfolio.domain.enums.MessageStatus;
import com.pranit.workspace.shared.wrapper.PageResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public interface ContactMessageService {

    ContactMessageResponse submitMessage(@Valid ContactMessageRequest request, String ipAddress, String userAgent);

    ContactMessageResponse getMessageById(Long id);

    PageResponse<ContactMessageResponse> getAllMessages
            (@Min(0) int page, @Min(1) @Max(5) int size, String keyword, MessageStatus status, String sortBy, String sortDirection);

    ContactMessageResponse updateMessageStatus(Long id, @Valid ContactStatusUpdateRequest request);

    void deleteMessage(Long id);
}
