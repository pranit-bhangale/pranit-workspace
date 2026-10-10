package com.pranit.workspace.auth.application.scheduler;

import com.pranit.workspace.auth.application.repository.UserRepository;
import com.pranit.workspace.auth.domain.entity.User;
import com.pranit.workspace.auth.domain.enums.AccountStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class InactiveAccountCleanupScheduler {

    private static final int RETENTION_DAYS = 1;
    private final UserRepository userRepository;

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void cleanupOnStartup() {
        cleanupUnverifiedRegistrations();
    }

    private void cleanupUnverifiedRegistrations() {
        final Instant cutoff = Instant.now().minus(RETENTION_DAYS, ChronoUnit.DAYS);
        final List<User> expiredUsers = userRepository.findByStatusAndCreatedAtBefore
                (AccountStatus.PENDING_VERIFICATION, cutoff);
        if (expiredUsers.isEmpty()) {
            log.info("Registration cleanup completed. No expired registrations.");
            return;
        }
        final int deletedCount = expiredUsers.size();
        userRepository.deleteAll(expiredUsers);
        log.info("Registration cleanup completed. Deleted {} expired registrations.", deletedCount);
    }

    @Scheduled(cron = "0 0 2 * * *", zone = "Asia/Kolkata")
    @Transactional
    public void cleanupDaily() {
        cleanupUnverifiedRegistrations();
    }
}
