package com.pranit.workspace.portfolio.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Encapsulates incoming inquiry sender identity.
 */
@Embeddable
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class ContactSender {

    @Column(name = "sender_name", nullable = false, length = 100)
    private String name;

    @Column(name = "sender_email", nullable = false, length = 100)
    private String email;

    @Column(name = "sender_phone", length = 30)
    private String phone;

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(name = "user_agent", length = 255)
    private String userAgent;
}