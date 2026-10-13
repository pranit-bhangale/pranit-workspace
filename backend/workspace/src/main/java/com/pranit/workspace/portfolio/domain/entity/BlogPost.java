package com.pranit.workspace.portfolio.domain.entity;

import com.pranit.workspace.portfolio.domain.enums.PostStatus;
import com.pranit.workspace.shared.audit.entity.Auditable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.BatchSize;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "blog_posts", indexes = {
        @Index(name = "idx_blog_posts_user_id", columnList = "user_id"),
        @Index(name = "idx_blog_posts_slug", columnList = "slug", unique = true),
        @Index(name = "idx_blog_posts_status", columnList = "status"),
        @Index(name = "idx_blog_posts_order", columnList = "display_order")
})
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@SuperBuilder
public class BlogPost extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "title", nullable = false, unique = true)
    private String title;

    @Column(name = "slug", nullable = false, unique = true, length = 255)
    private String slug;

    @Column(name = "excerpt", columnDefinition = "TEXT")
    private String excerpt;

    @Column(name = "content_markdown", columnDefinition = "TEXT")
    private String contentMarkdown;

    @Column(name = "publish_date")
    private LocalDate publishDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    @Builder.Default
    private PostStatus status = PostStatus.DRAFT;

    @BatchSize(size = 25)
    @ManyToMany
    @JoinTable(
            name = "blog_tags",
            joinColumns = @JoinColumn(
                    name = "blog_id",
                    foreignKey = @ForeignKey(name = "fk_blog_tag")
            ),
            inverseJoinColumns = @JoinColumn(
                    name = "tag_id",
                    foreignKey = @ForeignKey(name = "fk_tag_blog")
            )
    )
    @Builder.Default
    private List<Tag> tags = new ArrayList<>();

    @Column(name = "external_link", length = 2048)
    private String externalLink;

    @Column(name = "display_order", nullable = false)
    @Builder.Default
    private Integer displayOrder = 0;
}