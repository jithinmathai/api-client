package com.test.demo.service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

import com.test.demo.dto.SessionData;

@Service
public class TokenManagementService {

    private final Map<String, SessionData> tokenStore = new ConcurrentHashMap<>();

    public void storeToken(String sessionId, SessionData sessionData) {
        tokenStore.put(sessionId, sessionData);
    }

    public SessionData getToken(String sessionId) {
        return tokenStore.get(sessionId);
    }

    public boolean isTokenExpired(String sessionId) {
        SessionData data = tokenStore.get(sessionId);
        return data == null || data.getExpiresAt().isBefore(LocalDateTime.now());
    }

    public void removeToken(String sessionId) {
        tokenStore.remove(sessionId);
    }

    public void refreshToken(String sessionId) {
        SessionData data = tokenStore.get(sessionId);
        if (data != null) {
            data.setExpiresAt(LocalDateTime.now().plusHours(1));
        }
    }
}


