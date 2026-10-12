package com.pranit.workspace.portfolio.application.repository;

import com.pranit.workspace.portfolio.domain.entity.CodingAchievement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface CodingAchievementRepository extends JpaRepository<CodingAchievement, Long>, JpaSpecificationExecutor<CodingAchievement> {
}
