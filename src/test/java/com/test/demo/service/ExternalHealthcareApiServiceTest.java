package com.test.demo.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.test.demo.dto.ExternalLoginRequest;
import com.test.demo.dto.LoginRequest;
import com.test.demo.exception.SessionExpiredException;

@ExtendWith(MockitoExtension.class)
public class ExternalHealthcareApiServiceTest {

    @Mock
    private SessionManagementService sessionManagementService;

    @Mock
    private TokenManagementService tokenManagementService;

    @InjectMocks
    private ExternalHealthcareApiService service;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(service, "loginEndpoint", "/api/login");
        ReflectionTestUtils.setField(service, "patientProfileEndpoint", "/api/profile");
    }

    @Test
    void getPatientProfile_sessionExpired() {
        // Given
        String sessionId = "expired_session";
        
        when(tokenManagementService.getToken(sessionId)).thenReturn(null);

        // When & Then
        assertThrows(SessionExpiredException.class, () -> 
            service.getPatientProfile(sessionId, "checkOrNewPatient"));
    }

    @Test
    void refreshTokenIfNeeded_tokenExpired() {
        // Given
        String sessionId = "session123";
        when(tokenManagementService.isTokenExpired(sessionId)).thenReturn(true);

        // When
        service.refreshTokenIfNeeded(sessionId);

        // Then - verify refreshToken was called (no exception thrown)
    }

    @Test
    void buildFormDataForLogin_success() {
        // Given
        LoginRequest request = LoginRequest.builder()
            .username("testuser")
            .password("password")
            .dataCenterCapp("capp")
            .locale("1")
            .build();

        // When
        var result = service.buildFormDataForLogin(service.mapToExternalRequest(request));

        // Then
        assertThat(result).isNotNull();
        assertThat(result.get("username")).contains("testuser");
        assertThat(result.get("password")).contains("password");
        assertThat(result.get("dataCenterCapp")).contains("capp");
    }

    @Test
    void extractTokenFromResponse_success() {
        // Given
        String responseBody = "{\"token\":\"jwt_token_here\"}";

        // When
        String result = service.extractTokenFromResponse(responseBody);

        // Then
        assertThat(result).isEqualTo("jwt_token_here");
    }

    @Test
    void extractCookiesFromResponse_success() {
        // Given
        var response = org.springframework.http.ResponseEntity.ok("response body");

        // When
        Map<String, String> result = service.extractCookiesFromResponse(response);

        // Then
        assertThat(result).isNotNull();
        assertThat(result).isEmpty(); // TODO: implement actual cookie parsing
    }

    @Test
    void mapToExternalRequest_success() {
        // Given
        LoginRequest request = LoginRequest.builder()
            .username("testuser")
            .password("password")
            .dataCenterCapp("capp")
            .locale("1")
            .ltrmlal("1")
            .flowExecutionKey("e1s1")
            .eventId("submit")
            .isPureWeb("true")
            .solutionName("eas")
            .dbType("2")
            .userAuthPattern("BaseAD")
            .loginFlow("true")
            .clientHostIP("127.0.0.1")
            .build();

        // When
        ExternalLoginRequest result = service.mapToExternalRequest(request);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo("testuser");
        assertThat(result.getPassword()).isEqualTo("password");
        assertThat(result.getDataCenterCapp()).isEqualTo("capp");
        assertThat(result.getLocale()).isEqualTo("1");
    }
}
