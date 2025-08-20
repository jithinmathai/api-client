package com.test.demo.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthenticationResponse {
    private boolean success;
    private String token;
    private String subscriptionKey;
    private Long expiresIn;
    private String sessionId;
    private String message;
}


