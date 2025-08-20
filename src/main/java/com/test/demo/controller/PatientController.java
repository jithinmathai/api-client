package com.test.demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.test.demo.dto.PatientProfile;
import com.test.demo.service.ExternalHealthcareApiService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/patient")
@Slf4j
@RequiredArgsConstructor
public class PatientController {

    private final ExternalHealthcareApiService externalHealthcareApiService;

    @GetMapping("/profile")
    public ResponseEntity<PatientProfile> getProfile(
        @RequestParam String sessionId,
        @RequestParam(defaultValue = "checkOrNewPatient") String method
    ) {
        return ResponseEntity.ok(externalHealthcareApiService.getPatientProfile(sessionId, method));
    }

    @GetMapping("/profile/{hkid}")
    public ResponseEntity<PatientProfile> getSpecific(@PathVariable String hkid, @RequestParam String sessionId) {
        // TODO: integrate HKID filter with external API once spec is available.
        return ResponseEntity.ok(externalHealthcareApiService.getPatientProfile(sessionId, "checkOrNewPatient"));
    }
}


