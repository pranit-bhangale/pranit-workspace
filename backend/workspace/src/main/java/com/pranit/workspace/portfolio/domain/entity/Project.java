package com.pranit.workspace.portfolio.domain.entity;

import com.pranit.workspace.portfolio.domain.enums.ProjectCategory;
import com.pranit.workspace.portfolio.domain.enums.ProjectStatus;
import com.pranit.workspace.shared.audit.entity.Auditable;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
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
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import org.hibernate.annotations.BatchSize;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "projects", indexes = {
        @Index(name = "idx_projects_user_id", columnList = "user_id"),
        @Index(name = "idx_projects_category", columnList = "category"),
        @Index(name = "idx_projects_status", columnList = "status"),
        @Index(name = "idx_projects_featured", columnList = "is_featured"),
        @Index(name = "idx_projects_order", columnList = "display_order")
})
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@SuperBuilder
public class Project extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "title", nullable = false, length = 150)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false, length = 50)
    private ProjectCategory category;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private ProjectStatus status;

    @Column(name = "description", columnDefinition = "TEXT", nullable = false)
    private String description;
    
    @Embedded
    private ProjectAssets assets;

    @BatchSize(size = 25)
    @ManyToMany
    @JoinTable(
            name = "project_tech_stacks",
            joinColumns = @JoinColumn(
                    name = "project_id",
                    nullable = false,
                    foreignKey = @ForeignKey(name = "fk_project_tech_stack")
            ),
            inverseJoinColumns = @JoinColumn(
                    name = "tech_stack_id",
                    nullable = false,
                    foreignKey = @ForeignKey(name = "fk_tech_stack_project")
            )
    )
    @Builder.Default
    private List<TechStack> techStacks = new ArrayList<>();

    @BatchSize(size = 25)
    @ManyToMany
    @JoinTable(
            name = "project_features",
            joinColumns = @JoinColumn(
                    name = "project_id",
                    nullable = false,
                    foreignKey = @ForeignKey(name = "fk_project_feature")
            ),
            inverseJoinColumns = @JoinColumn(
                    name = "feature_id",
                    nullable = false,
                    foreignKey = @ForeignKey(name = "fk_feature_project")
            )
    )
    @Builder.Default
    private List<Feature> features = new ArrayList<>();

    @Column(name = "is_featured", nullable = false)
    @Builder.Default
    private Boolean isFeatured = false;

    @BatchSize(size = 25)
    @ManyToMany
    @JoinTable(
            name = "project_tags",
            joinColumns = @JoinColumn(
                    name = "project_id",
                    foreignKey = @ForeignKey(name = "fk_project_tag")
            ),
            inverseJoinColumns = @JoinColumn(
                    name = "tag_id",
                    foreignKey = @ForeignKey(name = "fk_tag_project")
            )
    )
    @Builder.Default
    private List<Tag> tags = new ArrayList<>();

    @Column(name = "display_order", nullable = false)
    @Builder.Default
    private Integer displayOrder = 0;
}