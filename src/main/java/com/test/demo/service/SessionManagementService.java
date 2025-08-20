package com.test.demo.service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.test.demo.dto.SessionData;

@Service
public class SessionManagementService {

    private final Map<String, SessionData> sessionStore = new ConcurrentHashMap<>();

    @Value("${session.default-expiry:3600}")
    private long defaultExpirySeconds;

    public String createSession(String username, String token, String subscriptionKey, Map<String, String> cookies) {
        String sessionId = generateSessionId();
        SessionData sessionData = SessionData.builder()
            .username(username)
            .token(token)
            .subscriptionKey(subscriptionKey)
            .cookies(cookies)
            .expiresAt(LocalDateTime.now().plusSeconds(defaultExpirySeconds))
            .build();
        sessionStore.put(sessionId, sessionData);
        return sessionId;
    }

    public SessionData getSession(String sessionId) {
        return sessionStore.get(sessionId);
    }

    public boolean isSessionValid(String sessionId) {
        SessionData data = sessionStore.get(sessionId);
        return data != null && data.getExpiresAt().isAfter(LocalDateTime.now());
    }

    public void removeExpiredSessions() {
        sessionStore.entrySet().removeIf(e -> e.getValue().getExpiresAt().isBefore(LocalDateTime.now()));
    }

    public String generateSessionId() {
        return UUID.randomUUID().toString();
    }
}


