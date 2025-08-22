package com.test.demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.test.demo.dto.ApiResponse;
import com.test.demo.dto.ChitNumberRequest;
import com.test.demo.dto.ChitNumberResponse;
import com.test.demo.dto.ProfileRequest;
import com.test.demo.dto.ProfileResponse;
import com.test.demo.service.ExternalHealthcareApiService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/qcare")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Qcare Services", description = "Healthcare service endpoints using QcareRestService")
public class QcareController {

    private final ExternalHealthcareApiService externalHealthcareApiService;

    @PostMapping("/profile")
    @Operation(summary = "Create Patient Profile", description = "Create or update patient profile information")
    public ResponseEntity<ApiResponse<ProfileResponse>> createProfile(
            @RequestHeader("Session-Id") String sessionId,
            @Valid @RequestBody ProfileRequest profileRequest) {
        
        log.info("Creating patient profile for session: {}", sessionId);
        
        try {
            ProfileResponse profileResponse = externalHealthcareApiService.createPatientProfile(sessionId, profileRequest);
            
            return ResponseEntity.ok(
                ApiResponse.success("Profile created successfully", profileResponse)
            );
        } catch (Exception e) {
            log.error("Failed to create profile for session {}: {}", sessionId, e.getMessage());
            return ResponseEntity.badRequest().body(
                ApiResponse.failure(400, "Failed to create profile: " + e.getMessage())
            );
        }
    }

    @PostMapping("/chit")
    @Operation(summary = "Generate Chit Number", description = "Generate a chit number for patient services")
    public ResponseEntity<ApiResponse<ChitNumberResponse>> generateChitNumber(
            @RequestHeader("Session-Id") String sessionId,
            @Valid @RequestBody ChitNumberRequest chitRequest) {
        
        log.info("Generating chit number for session: {}", sessionId);
        
        try {
            ChitNumberResponse chitResponse = externalHealthcareApiService.generateChitNumber(sessionId, chitRequest);
            
            return ResponseEntity.ok(
                ApiResponse.success("Chit number generated successfully", chitResponse)
            );
        } catch (Exception e) {
            log.error("Failed to generate chit number for session {}: {}", sessionId, e.getMessage());
            return ResponseEntity.badRequest().body(
                ApiResponse.failure(400, "Failed to generate chit number: " + e.getMessage())
            );
        }
    }
}
