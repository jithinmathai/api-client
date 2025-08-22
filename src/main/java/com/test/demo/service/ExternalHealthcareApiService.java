package com.test.demo.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import com.test.demo.client.QcareRestService;
import com.test.demo.dto.ApiResponse;
import com.test.demo.dto.AuthenticationResponse;
import com.test.demo.dto.ChitNumberRequest;
import com.test.demo.dto.ChitNumberResponse;
import com.test.demo.dto.ExternalLoginRequest;
import com.test.demo.dto.LoginRequest;
import com.test.demo.dto.PatientProfile;
import com.test.demo.dto.ProfileRequest;
import com.test.demo.dto.ProfileResponse;
import com.test.demo.dto.SessionData;
import com.test.demo.exception.AuthenticationFailedException;
import com.test.demo.exception.ExternalApiException;
import com.test.demo.exception.SessionExpiredException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class ExternalHealthcareApiService {

    private final QcareRestService qcareRestService;
    private final RestClient externalApiRestClient; // Keep for backward compatibility
    private final SessionManagementService sessionManagementService;
    private final TokenManagementService tokenManagementService;

    @Value("${external.healthcare.login-endpoint}")
    private String loginEndpoint;

    @Value("${external.healthcare.patient-profile-endpoint}")
    private String patientProfileEndpoint;

    public AuthenticationResponse authenticateWithExternalSystem(LoginRequest request) {
        try {
            // Use the new QcareRestService interface
            ProfileRequest profileRequest = mapToProfileRequest(request);
            
            ResponseEntity<ApiResponse<ProfileResponse>> response = qcareRestService.login(profileRequest);
            
            ApiResponse<ProfileResponse> apiResponse = response.getBody();
            if (apiResponse == null || !apiResponse.isSuccess()) {
                String errorMsg = apiResponse != null ? apiResponse.getMsg() : "No response received";
                throw new AuthenticationFailedException("Login failed: " + errorMsg);
            }
            
            ProfileResponse profileResponse = apiResponse.getData();
            if (profileResponse == null || profileResponse.getToken() == null) {
                throw new AuthenticationFailedException("No token received in successful login response");
            }
            
            Map<String, String> cookies = extractCookiesFromResponse(response);
            String sessionId = sessionManagementService.createSession(
                request.getUsername(), 
                profileResponse.getToken(), 
                profileResponse.getSubscriptionKey(), 
                cookies
            );
            
            SessionData sessionData = sessionManagementService.getSession(sessionId);
            tokenManagementService.storeToken(sessionId, sessionData);
            
            return AuthenticationResponse.builder()
                .success(true)
                .token(profileResponse.getToken())
                .subscriptionKey(profileResponse.getSubscriptionKey())
                .expiresIn(profileResponse.getExpiresIn() != null ? profileResponse.getExpiresIn() : 3600L)
                .sessionId(sessionId)
                .message(profileResponse.getMessage() != null ? profileResponse.getMessage() : "Authenticated")
                .build();
        } catch (Exception e) {
            log.error("Authentication failed", e);
            throw new AuthenticationFailedException("External authentication failed", e);
        }
    }

    public PatientProfile getPatientProfile(String sessionId, String method) {
        refreshTokenIfNeeded(sessionId);
        SessionData session = tokenManagementService.getToken(sessionId);
        if (session == null) {
            throw new SessionExpiredException("Session not found or expired");
        }

        try {
            ResponseEntity<ApiResponse<PatientProfile>> response = externalApiRestClient.get()
                .uri(uriBuilder -> uriBuilder
                    .path(patientProfileEndpoint)
                    .queryParam("method", method != null ? method : "checkOrNewPatient")
                    .build())
                .headers(headers -> {
                    if (session.getCookies() != null) {
                        StringBuilder cookieHeader = new StringBuilder();
                        session.getCookies().forEach((k, v) -> cookieHeader.append(k).append("=").append(v).append("; "));
                        headers.add(HttpHeaders.COOKIE, cookieHeader.toString());
                    }
                })
                .retrieve()
                .toEntity(new org.springframework.core.ParameterizedTypeReference<ApiResponse<PatientProfile>>() {});
                
            ApiResponse<PatientProfile> apiResponse = response.getBody();
            if (apiResponse == null || !apiResponse.isSuccess()) {
                String errorMsg = apiResponse != null ? apiResponse.getMsg() : "No response received";
                throw new ExternalApiException("Failed to fetch patient profile: " + errorMsg);
            }
            
            return apiResponse.getData();
        } catch (Exception e) {
            log.error("Failed to fetch patient profile", e);
            throw new ExternalApiException("Failed to fetch patient profile", e);
        }
    }

    public void refreshTokenIfNeeded(String sessionId) {
        if (tokenManagementService.isTokenExpired(sessionId)) {
            tokenManagementService.refreshToken(sessionId);
        }
    }

    public MultiValueMap<String, String> buildFormDataForLogin(ExternalLoginRequest request) {
        LinkedMultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("locale", request.getLocale());
        form.add("dataCenterCapp", request.getDataCenterCapp());
        form.add("username", request.getUsername());
        form.add("password", request.getPassword());
        form.add("ltrmlal", request.getLtrmlal());
        form.add("_flowExecutionKey", request.getFlowExecutionKey());
        form.add("_eventId", request.getEventId());
        form.add("isPureWeb", request.getIsPureWeb());
        form.add("solutionName", request.getSolutionName());
        form.add("dbType", request.getDbType());
        form.add("userAuthPattern", request.getUserAuthPattern());
        form.add("loginFlow", request.getLoginFlow());
        form.add("clientHostIP", request.getClientHostIP());
        return form;
    }

    public String extractTokenFromResponse(String responseBody) {
        // This method is now deprecated since we're using ApiResponse<String> directly
        // The token is now extracted from ApiResponse.getData()
        return responseBody;
    }

    public Map<String, String> extractCookiesFromResponse(ResponseEntity<?> response) {
        Map<String, String> cookies = new HashMap<>();
        if (response.getHeaders().containsKey("Set-Cookie")) {
            response.getHeaders().get("Set-Cookie").forEach(cookie -> {
                String[] parts = cookie.split(";")[0].split("=", 2);
                if (parts.length == 2) {
                    cookies.put(parts[0].trim(), parts[1].trim());
                }
            });
        }
        return cookies;
    }

    public ExternalLoginRequest mapToExternalRequest(LoginRequest request) {
        return ExternalLoginRequest.builder()
            .username(request.getUsername())
            .password(request.getPassword())
            .dataCenterCapp(request.getDataCenterCapp())
            .locale(request.getLocale())
            .ltrmlal(request.getLtrmlal())
            .flowExecutionKey(request.getFlowExecutionKey())
            .eventId(request.getEventId())
            .isPureWeb(request.getIsPureWeb())
            .solutionName(request.getSolutionName())
            .dbType(request.getDbType())
            .userAuthPattern(request.getUserAuthPattern())
            .loginFlow(request.getLoginFlow())
            .clientHostIP(request.getClientHostIP())
            .build();
    }

    public ProfileRequest mapToProfileRequest(LoginRequest request) {
        return ProfileRequest.builder()
            .username(request.getUsername())
            .password(request.getPassword())
            .dataCenterCapp("capp")
            .loginFlow("eassso")
            .clientHostIP("127.0.0.1")
            .method("login")
            .build();
    }

    // New methods using QcareRestService
    public ProfileResponse createPatientProfile(String sessionId, ProfileRequest profileRequest) {
        refreshTokenIfNeeded(sessionId);
        SessionData session = tokenManagementService.getToken(sessionId);
        if (session == null) {
            throw new SessionExpiredException("Session not found or expired");
        }

        try {
            // Set session info in the request
            profileRequest.setSessionId(sessionId);
            profileRequest.setToken(session.getToken());
            
            ResponseEntity<ApiResponse<ProfileResponse>> response = qcareRestService.createProfile(profileRequest);
            
            ApiResponse<ProfileResponse> apiResponse = response.getBody();
            if (apiResponse == null || !apiResponse.isSuccess()) {
                String errorMsg = apiResponse != null ? apiResponse.getMsg() : "No response received";
                throw new ExternalApiException("Failed to create patient profile: " + errorMsg);
            }
            
            return apiResponse.getData();
        } catch (Exception e) {
            log.error("Failed to create patient profile", e);
            throw new ExternalApiException("Failed to create patient profile", e);
        }
    }

    public ChitNumberResponse generateChitNumber(String sessionId, ChitNumberRequest chitRequest) {
        refreshTokenIfNeeded(sessionId);
        SessionData session = tokenManagementService.getToken(sessionId);
        if (session == null) {
            throw new SessionExpiredException("Session not found or expired");
        }

        try {
            // Set session info in the request
            chitRequest.setSessionId(sessionId);
            chitRequest.setToken(session.getToken());
            
            ResponseEntity<ApiResponse<ChitNumberResponse>> response = qcareRestService.createChitNum(chitRequest);
            
            ApiResponse<ChitNumberResponse> apiResponse = response.getBody();
            if (apiResponse == null || !apiResponse.isSuccess()) {
                String errorMsg = apiResponse != null ? apiResponse.getMsg() : "No response received";
                throw new ExternalApiException("Failed to generate chit number: " + errorMsg);
            }
            
            return apiResponse.getData();
        } catch (Exception e) {
            log.error("Failed to generate chit number", e);
            throw new ExternalApiException("Failed to generate chit number", e);
        }
    }
}


