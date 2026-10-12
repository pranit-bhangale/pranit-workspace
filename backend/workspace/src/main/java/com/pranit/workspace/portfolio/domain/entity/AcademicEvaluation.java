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
 * Encapsulates academic evaluation metrics.
 */
@Embeddable
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class AcademicEvaluation {

    @Column(name = "grade_value", length = 50)
    private String gradeValue;

    @Column(name = "grade_scale", length = 50)
    private String gradeScale;

    @Column(name = "distinction_status", length = 100)
    private String distinctionStatus;
}