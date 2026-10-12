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
 * Encapsulates media showcases for a project.
 */
@Embeddable
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class ProjectAssets {

    @Column(name = "demo_link", length = 500)
    private String demoLink;

    @Column(name = "github_link", length = 500)
    private String githubLink;

    @Column(name = "documentation_url", length = 500)
    private String documentationUrl;

    @Column(name = "video_url", length = 500)
    private String videoUrl;

    @Column(name = "document_url", length = 500)
    private String documentUrl;

    @Column(name = "image_url", length = 500)
    private String imageUrl;
}