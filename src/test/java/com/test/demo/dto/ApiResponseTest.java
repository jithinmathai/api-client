package com.test.demo.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class ApiResponseTest {

    @Test
    void success_createsSuccessfulResponse() {
        // Given
        String testData = "test data";
        String message = "Operation successful";

        // When
        ApiResponse<String> response = ApiResponse.success(message, testData);

        // Then
        assertThat(response.getCode()).isEqualTo(0);
        assertThat(response.getMsg()).isEqualTo(message);
        assertThat(response.getData()).isEqualTo(testData);
        assertThat(response.isSuccess()).isTrue();
    }

    @Test
    void failure_createsFailureResponse() {
        // Given
        int errorCode = 400;
        String errorMessage = "Bad Request";

        // When
        ApiResponse<String> response = ApiResponse.failure(errorCode, errorMessage);

        // Then
        assertThat(response.getCode()).isEqualTo(errorCode);
        assertThat(response.getMsg()).isEqualTo(errorMessage);
        assertThat(response.getData()).isNull();
        assertThat(response.isSuccess()).isFalse();
    }

    @Test
    void failureWithData_createsFailureResponseWithData() {
        // Given
        int errorCode = 422;
        String errorMessage = "Validation failed";
        String errorData = "Invalid field: username";

        // When
        ApiResponse<String> response = ApiResponse.failure(errorCode, errorMessage, errorData);

        // Then
        assertThat(response.getCode()).isEqualTo(errorCode);
        assertThat(response.getMsg()).isEqualTo(errorMessage);
        assertThat(response.getData()).isEqualTo(errorData);
        assertThat(response.isSuccess()).isFalse();
    }

    @Test
    void isSuccess_returnsTrueOnlyForCodeZero() {
        // Given & When & Then
        assertThat(ApiResponse.success("ok", "data").isSuccess()).isTrue();
        assertThat(ApiResponse.failure(1, "error").isSuccess()).isFalse();
        assertThat(ApiResponse.failure(-1, "error").isSuccess()).isFalse();
        assertThat(ApiResponse.failure(200, "error").isSuccess()).isFalse();
    }

    @Test
    void builder_createsResponseCorrectly() {
        // Given & When
        ApiResponse<PatientProfile> response = ApiResponse.<PatientProfile>builder()
            .code(0)
            .msg("Patient found")
            .data(PatientProfile.builder().hkid("P1234567").build())
            .build();

        // Then
        assertThat(response.getCode()).isEqualTo(0);
        assertThat(response.getMsg()).isEqualTo("Patient found");
        assertThat(response.getData()).isNotNull();
        assertThat(response.getData().getHkid()).isEqualTo("P1234567");
        assertThat(response.isSuccess()).isTrue();
    }
}
