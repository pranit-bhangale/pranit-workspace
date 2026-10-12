package com.pranit.workspace.portfolio.application.repository;

import com.pranit.workspace.portfolio.domain.entity.ContactMessage;
import com.pranit.workspace.portfolio.domain.enums.MessageStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ContactMessageRepository extends JpaRepository<ContactMessage, Long>, JpaSpecificationExecutor<ContactMessage> {

    Page<ContactMessage> findByStatus(MessageStatus status, Pageable pageable);
}
