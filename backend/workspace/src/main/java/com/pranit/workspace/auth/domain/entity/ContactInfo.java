package com.pranit.workspace.auth.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Encapsulates direct communication coordinates.
 */
@Embeddable
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class ContactInfo {

    @Column(name = "contact_email", length = 100)
    private String email;

    @Column(name = "contact_phone", length = 30)
    private String phone;

    @Column(name = "whatsapp_number", length = 30)
    private String whatsappNumber;
}
