package com.test.demo.integration;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.test.demo.dto.ApiResponse;
import com.test.demo.dto.PatientProfile;

@SpringBootTest
public class ApiResponseIntegrationTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void testApiResponseSerialization() throws Exception {
        // Given
        PatientProfile patient = PatientProfile.builder()
            .hkid("P1234567")
            .firstname("John")
            .lastname("Doe")
            .gender("Male")
            .email("john.doe@example.com")
            .build();

        ApiResponse<PatientProfile> apiResponse = ApiResponse.success("Patient retrieved successfully", patient);

        // When - Serialize to JSON
        String json = objectMapper.writeValueAsString(apiResponse);

        // Then - Verify JSON structure matches expected format
        assertThat(json).contains("\"code\":0");
        assertThat(json).contains("\"msg\":\"Patient retrieved successfully\"");
        assertThat(json).contains("\"data\":");
        assertThat(json).contains("\"hkid\":\"P1234567\"");

        // When - Deserialize back from JSON
        ApiResponse<PatientProfile> deserializedResponse = objectMapper.readValue(json, 
            objectMapper.getTypeFactory().constructParametricType(ApiResponse.class, PatientProfile.class));

        // Then - Verify deserialization works correctly
        assertThat(deserializedResponse.isSuccess()).isTrue();
        assertThat(deserializedResponse.getCode()).isEqualTo(0);
        assertThat(deserializedResponse.getMsg()).isEqualTo("Patient retrieved successfully");
        assertThat(deserializedResponse.getData()).isNotNull();
        assertThat(deserializedResponse.getData().getHkid()).isEqualTo("P1234567");
        assertThat(deserializedResponse.getData().getFirstname()).isEqualTo("John");
    }

    @Test
    void testApiResponseErrorSerialization() throws Exception {
        // Given
        ApiResponse<String> errorResponse = ApiResponse.failure(400, "Invalid request parameters");

        // When - Serialize to JSON
        String json = objectMapper.writeValueAsString(errorResponse);

        // Then - Verify error JSON structure
        assertThat(json).contains("\"code\":400");
        assertThat(json).contains("\"msg\":\"Invalid request parameters\"");
        assertThat(json).doesNotContain("\"data\""); // Should be excluded due to @JsonInclude(NON_NULL)

        // When - Deserialize back from JSON
        ApiResponse<String> deserializedResponse = objectMapper.readValue(json, 
            objectMapper.getTypeFactory().constructParametricType(ApiResponse.class, String.class));

        // Then - Verify error deserialization
        assertThat(deserializedResponse.isSuccess()).isFalse();
        assertThat(deserializedResponse.getCode()).isEqualTo(400);
        assertThat(deserializedResponse.getMsg()).isEqualTo("Invalid request parameters");
        assertThat(deserializedResponse.getData()).isNull();
    }

    @Test
    void testResponseEntityWithApiResponse() {
        // Given
        PatientProfile patient = PatientProfile.builder()
            .hkid("P7654321")
            .firstname("Jane")
            .lastname("Smith")
            .build();

        // When - Create ResponseEntity with ApiResponse (simulating external API response)
        ResponseEntity<ApiResponse<PatientProfile>> responseEntity = ResponseEntity.ok(
            ApiResponse.success("Patient found", patient)
        );

        // Then - Verify the structure matches what we expect from external API
        assertThat(responseEntity.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(responseEntity.getBody()).isNotNull();
        assertThat(responseEntity.getBody().isSuccess()).isTrue();
        assertThat(responseEntity.getBody().getData().getHkid()).isEqualTo("P7654321");
    }

    @Test
    void demonstrateExternalApiResponseFormat() {
        // This test demonstrates the expected format from the external healthcare API
        
        // Example 1: Successful login response
        ApiResponse<String> loginSuccess = ApiResponse.success("Login successful", "jwt_token_abc123");
        assertThat(loginSuccess.getCode()).isEqualTo(0);
        assertThat(loginSuccess.getMsg()).isEqualTo("Login successful");
        assertThat(loginSuccess.getData()).isEqualTo("jwt_token_abc123");

        // Example 2: Failed login response  
        ApiResponse<String> loginFailure = ApiResponse.failure(401, "Invalid credentials");
        assertThat(loginFailure.getCode()).isEqualTo(401);
        assertThat(loginFailure.getMsg()).isEqualTo("Invalid credentials");
        assertThat(loginFailure.getData()).isNull();

        // Example 3: Patient profile response
        PatientProfile profile = PatientProfile.builder()
            .hkid("P6467494")
            .passportNo("")
            .lastname("testone")
            .firstname("testone")
            .gender("Female")
            .dob("20061201")
            .contactNo("29761234")
            .address("HK")
            .email("samson_ye@kingdom.com")
            .language("English")
            .isBlue("Y")
            .optOut("")
            .build();

        ApiResponse<PatientProfile> profileResponse = ApiResponse.success("Patient profile retrieved", profile);
        assertThat(profileResponse.isSuccess()).isTrue();
        assertThat(profileResponse.getData().getHkid()).isEqualTo("P6467494");
        assertThat(profileResponse.getData().getGender()).isEqualTo("Female");
    }
}
