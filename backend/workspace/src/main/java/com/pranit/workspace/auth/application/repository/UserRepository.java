package com.pranit.workspace.auth.application.repository;

import com.pranit.workspace.auth.domain.entity.User;
import com.pranit.workspace.auth.domain.enums.AccountStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    @EntityGraph(attributePaths = {"roles", "profile"})
    Optional<User> findByUsernameAndEnabledTrue(String username);

    Optional<User> findByUsername(String username);

    @Query("""
            SELECT u
            FROM User u
            LEFT JOIN FETCH u.profile p
            WHERE u.userId = :userId
            """)
    Optional<User> findByUserIdWithProfile(@Param("userId") UUID userId);

    Optional<User> findByUserId(UUID userId);
    
    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);

    List<User> findByStatusAndCreatedAtBefore(AccountStatus status, Instant cutoff);
}
