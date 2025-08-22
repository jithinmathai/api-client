package com.test.demo.client;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

import com.test.demo.dto.ApiResponse;
import com.test.demo.dto.ChitNumberRequest;
import com.test.demo.dto.ChitNumberResponse;
import com.test.demo.dto.ProfileRequest;
import com.test.demo.dto.ProfileResponse;

@HttpExchange("/api/phygital")
public interface QcareRestService {

    @GetExchange(url = "/capp/eassso/login")
    ResponseEntity<ApiResponse<ProfileResponse>> login(@RequestBody ProfileRequest body);

    @PostExchange(url = "/easweb/lims/uf/patientProfile.do?method=checkOrNewPatient", contentType = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<ApiResponse<ProfileResponse>> createProfile(@RequestBody ProfileRequest body);

    @PostExchange(url = "/easweb/lims/uf/chit", contentType = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<ApiResponse<ChitNumberResponse>> createChitNum(@RequestBody ChitNumberRequest body);
}
