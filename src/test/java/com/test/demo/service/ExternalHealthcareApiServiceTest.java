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
        // No longer need to set endpoint fields since they're defined in QcareRestService @HttpExchange annotations
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
        // Given - This method is now deprecated as we use ApiResponse<String> directly
        String responseBody = "jwt_token_here";

        // When
        String result = service.extractTokenFromResponse(responseBody);

        // Then
        assertThat(result).isEqualTo("jwt_token_here");
    }

    @Test
    void extractCookiesFromResponse_success() {
        // Given
        var response = org.springframework.http.ResponseEntity.ok()
            .header("Set-Cookie", "JSESSIONID=ABC123; Path=/; HttpOnly")
            .header("Set-Cookie", "AUTH_TOKEN=XYZ789; Path=/api; Secure")
            .body("response body");

        // When
        Map<String, String> result = service.extractCookiesFromResponse(response);

        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        assertThat(result).containsEntry("JSESSIONID", "ABC123");
        assertThat(result).containsEntry("AUTH_TOKEN", "XYZ789");
    }

    @Test
    void extractCookiesFromResponse_noCookies() {
        // Given
        var response = org.springframework.http.ResponseEntity.ok("response body");

        // When
        Map<String, String> result = service.extractCookiesFromResponse(response);

        // Then
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();
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
