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
 * Encapsulates hero text and narrative bio.
 */
@Embeddable
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class AboutSection {

    @Column(name = "about_title", length = 100)
    private String title;

    @Column(name = "about_text", columnDefinition = "TEXT")
    private String text;
}
