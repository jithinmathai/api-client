package com.test.demo.task;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.test.demo.service.SessionManagementService;
import com.test.demo.service.TokenManagementService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class SessionCleanupTask {

    private final SessionManagementService sessionManagementService;
    private final TokenManagementService tokenManagementService;

    @Scheduled(fixedDelayString = "${session.cleanup.interval}")
    public void cleanupExpiredSessions() {
        sessionManagementService.removeExpiredSessions();
        // Tokens are stored separately; clear tokens with expired sessions
        // In this skeleton, sessions and tokens share lifecycle via session removal path.
        log.debug("Session cleanup executed");
    }
}


