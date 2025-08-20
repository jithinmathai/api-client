package com.test.demo.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.test.demo.dto.AuthenticationResponse;
import com.test.demo.dto.ExternalLoginRequest;
import com.test.demo.dto.LoginRequest;
import com.test.demo.dto.PatientProfile;
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

    private final HttpClientService httpClientService;
    private final SessionManagementService sessionManagementService;
    private final TokenManagementService tokenManagementService;

    @Value("${external.healthcare.login-endpoint}")
    private String loginEndpoint;

    @Value("${external.healthcare.patient-profile-endpoint}")
    private String patientProfileEndpoint;

    public AuthenticationResponse authenticateWithExternalSystem(LoginRequest request) {
        ExternalLoginRequest externalRequest = mapToExternalRequest(request);
        MultiValueMap<String, String> form = buildFormDataForLogin(externalRequest);

        HttpHeaders headers = new HttpHeaders();
        String body = httpClientService.postFormData(loginEndpoint, form, headers);
        String token = extractTokenFromResponse(body);
        Map<String, String> cookies = new HashMap<>();
        String subscriptionKey = "";
        if (token == null || token.isBlank()) {
            throw new AuthenticationFailedException("Failed to extract token from login response");
        }
        String sessionId = sessionManagementService.createSession(request.getUsername(), token, subscriptionKey, cookies);
        SessionData sessionData = sessionManagementService.getSession(sessionId);
        tokenManagementService.storeToken(sessionId, sessionData);
        return AuthenticationResponse.builder()
            .success(true)
            .token(token)
            .subscriptionKey(subscriptionKey)
            .expiresIn(3600L)
            .sessionId(sessionId)
            .message("Authenticated")
            .build();
    }

    public PatientProfile getPatientProfile(String sessionId, String method) {
        refreshTokenIfNeeded(sessionId);
        SessionData session = tokenManagementService.getToken(sessionId);
        if (session == null) {
            throw new SessionExpiredException("Session not found or expired");
        }

        Map<String, String> query = new HashMap<>();
        query.put("method", method != null ? method : "checkOrNewPatient");

        String body = httpClientService.getWithCookies(patientProfileEndpoint, session.getCookies(), query);
        try {
            // TODO: Map actual response to PatientProfile using ObjectMapper when schema known
            return PatientProfile.builder().build();
        } catch (Exception e) {
            throw new ExternalApiException("Failed to parse patient profile", e);
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
        // TODO: Implement proper token extraction based on actual response structure
        return "jwt_token_here";
    }

    public Map<String, String> extractCookiesFromResponse(ResponseEntity<String> response) {
        // TODO: parse Set-Cookie headers
        return new HashMap<>();
    }

    private ExternalLoginRequest mapToExternalRequest(LoginRequest request) {
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
}


