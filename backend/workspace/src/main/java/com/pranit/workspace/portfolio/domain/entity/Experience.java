package com.pranit.workspace.portfolio.domain.entity;

import com.pranit.workspace.portfolio.domain.enums.EmploymentType;
import com.pranit.workspace.portfolio.domain.enums.WorkplaceType;
import com.pranit.workspace.shared.audit.entity.Auditable;
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
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "experiences", indexes = {
        @Index(name = "idx_experiences_user_id", columnList = "user_id"),
        @Index(name = "idx_experiences_order", columnList = "display_order")
})
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@SuperBuilder
public class Experience extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "company", nullable = false, length = 150)
    private String company;

    @Column(name = "role", nullable = false, length = 100)
    private String role;

    @Enumerated(EnumType.STRING)
    @Column(name = "employment_type", nullable = false, length = 30)
    private EmploymentType employmentType;

    @Enumerated(EnumType.STRING)
    @Column(name = "workplace_type", nullable = false, length = 30)
    private WorkplaceType workplaceType;

    @Embedded
    private DatePeriod period;

    @Column(name = "description_markdown", columnDefinition = "TEXT")
    private String descriptionMarkdown;

    @ManyToMany
    @JoinTable(
            name = "experience_tech_stacks",
            joinColumns = @JoinColumn(
                    name = "experience_id",
                    nullable = false,
                    foreignKey = @ForeignKey(name = "fk_experience_tech_stack")
            ),
            inverseJoinColumns = @JoinColumn(
                    name = "tech_stack_id",
                    nullable = false,
                    foreignKey = @ForeignKey(name = "fk_tech_stack_experience")
            )
    )
    @Builder.Default
    private List<TechStack> techStacks = new ArrayList<>();

    @Column(name = "display_order", nullable = false)
    @Builder.Default
    private Integer displayOrder = 0;
}