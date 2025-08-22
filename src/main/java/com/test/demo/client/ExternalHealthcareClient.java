package com.test.demo.client;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

import com.test.demo.dto.ExternalLoginRequest;
import com.test.demo.dto.PatientProfile;

@HttpExchange("/api/phygital/capp")
public interface ExternalHealthcareClient {

    @PostExchange(url = "/eassso/login", contentType = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    ResponseEntity<String> login(@RequestBody ExternalLoginRequest request);

    @GetExchange(url = "/easweb/lims/uf/patientProfile.do")
    ResponseEntity<PatientProfile> getPatientProfile(@RequestBody String method);
}
