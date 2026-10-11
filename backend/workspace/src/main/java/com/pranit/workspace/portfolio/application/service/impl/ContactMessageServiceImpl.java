package com.pranit.workspace.portfolio.application.service.impl;

import com.pranit.workspace.portfolio.application.repository.ContactMessageRepository;
import com.pranit.workspace.portfolio.application.service.ContactMessageService;
import com.pranit.workspace.portfolio.application.specification.MessageSpecification;
import com.pranit.workspace.portfolio.domain.dto.ContactMessageRequest;
import com.pranit.workspace.portfolio.domain.dto.ContactMessageResponse;
import com.pranit.workspace.portfolio.domain.dto.ContactStatusUpdateRequest;
import com.pranit.workspace.portfolio.domain.entity.ContactMessage;
import com.pranit.workspace.portfolio.domain.entity.ContactSender;
import com.pranit.workspace.portfolio.domain.enums.MessageStatus;
import com.pranit.workspace.shared.exception.ResourceNotFoundException;
import com.pranit.workspace.shared.helper.Pagination;
import com.pranit.workspace.shared.wrapper.PageResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ContactMessageServiceImpl implements ContactMessageService {

    private final ContactMessageRepository contactMessageRepository;

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public ContactMessageResponse submitMessage
            (final ContactMessageRequest request, final String ipAddress, final String userAgent) {
        log.info("Submitting contact message from email: {}", request.senderEmail());
        final String safeIp = sanitizeString(ipAddress, 45);
        final String safeUserAgent = sanitizeString(userAgent, 255);
        final ContactSender sender = ContactSender.builder()
                .name(request.senderName().trim())
                .email(request.senderEmail().trim())
                .phone(request.senderPhone() != null ? request.senderPhone().trim() : null)
                .ipAddress(safeIp)
                .userAgent(safeUserAgent)
                .build();
        ContactMessage message = ContactMessage.builder()
                .sender(sender)
                .messageContent(request.messageContent().trim())
                .status(MessageStatus.UNREAD)
                .build();
        message = this.contactMessageRepository.save(message);
        return ContactMessageResponse.from(message);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public ContactMessageResponse getMessageById(final Long id) {
        final ContactMessage message = this.contactMessageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ContactMessage", "id", id));
        return ContactMessageResponse.from(message);
    }

    @Override
    public PageResponse<ContactMessageResponse> getAllMessages
            (final int page, final int size, final String keyword, final MessageStatus status,
             final String sortBy, final String sortDirection) {
        final Specification<ContactMessage> specification = Specification
                .where(MessageSpecification.searchKeyword(keyword))
                .and(MessageSpecification.hasStatus(status));
        return Pagination.search
                (this.contactMessageRepository, specification, page, size, sortBy, sortDirection, ContactMessageResponse::from);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public ContactMessageResponse updateMessageStatus(final Long id, final ContactStatusUpdateRequest request) {
        log.info("Updating contact message status id: {} to {}", id, request.status());
        final ContactMessage message = this.contactMessageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ContactMessage", "id", id));
        message.setStatus(request.status());
        return ContactMessageResponse.from(message);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteMessage(final Long id) {
        log.info("Deleting contact message id: {}", id);
        final ContactMessage message = this.contactMessageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ContactMessage", "id", id));
        this.contactMessageRepository.delete(message);
    }

    private String sanitizeString(final String value, final int maxLength) {
        if (value == null) return null;
        final String trimmed = value.trim();
        return trimmed.length() > maxLength
                ? trimmed.substring(0, maxLength) : trimmed;
    }
}
