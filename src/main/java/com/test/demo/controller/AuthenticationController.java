package com.test.demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.test.demo.dto.AuthenticationResponse;
import com.test.demo.dto.LoginRequest;
import com.test.demo.service.ExternalHealthcareApiService;
import com.test.demo.service.SessionManagementService;
import com.test.demo.service.TokenManagementService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/auth")
@Slf4j
@RequiredArgsConstructor
@Validated
public class AuthenticationController {

    private final ExternalHealthcareApiService externalHealthcareApiService;
    private final SessionManagementService sessionManagementService;
    private final TokenManagementService tokenManagementService;

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(externalHealthcareApiService.authenticateWithExternalSystem(request));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestParam String sessionId) {
        tokenManagementService.removeToken(sessionId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/status")
    public ResponseEntity<Boolean> status(@RequestParam String sessionId) {
        boolean valid = sessionManagementService.isSessionValid(sessionId);
        return ResponseEntity.ok(valid);
    }
}


