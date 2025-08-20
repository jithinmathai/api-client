package com.test.demo.dto;

import java.time.LocalDateTime;
import java.util.Map;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SessionData {
    private String token;
    private String subscriptionKey;
    private String username;
    private LocalDateTime expiresAt;
    private Map<String, String> cookies;
}


