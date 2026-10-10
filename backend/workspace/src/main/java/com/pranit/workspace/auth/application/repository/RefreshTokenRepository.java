package com.pranit.workspace.auth.application.repository;

import com.pranit.workspace.auth.domain.entity.RefreshToken;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            select rt
            from RefreshToken rt
            join fetch rt.user u
            where rt.jti = :jti
            """)
    Optional<RefreshToken> findByJtiForUpdate(@Param("jti") String jti);

    @Modifying
    @Query("""
            update RefreshToken rt
            set rt.revoked = true
            where rt.user.userId = :userId
              and rt.revoked = false
            """)
    void revokeAllByUserId(@Param("userId") UUID userId);
}
