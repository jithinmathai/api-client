package com.test.demo.config;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestClient;

import com.test.demo.util.JsonUtils;

@SpringBootTest
public class RestClientConfigTest {

    @Autowired
    private RestClient externalApiRestClient;

    @Autowired
    private RestClientProperties restClientProperties;

    @Autowired
    private JsonUtils jsonUtils;

    @Test
    void restClientBeanIsCreated() {
        // Then
        assertThat(externalApiRestClient).isNotNull();
    }

    @Test
    void restClientPropertiesAreLoaded() {
        // Then
        assertThat(restClientProperties).isNotNull();
        assertThat(restClientProperties.getBaseUrl()).isEqualTo("https://uatcloud-capp.lims.com");
        assertThat(restClientProperties.getTimeout()).isEqualTo(30000);
        assertThat(restClientProperties.getConnectionTimeout()).isEqualTo(10000);
        assertThat(restClientProperties.getUserAgent()).contains("Mozilla/5.0");
        assertThat(restClientProperties.getConnection()).isEqualTo("keep-alive");
    }

    @Test
    void jsonUtilsIsAvailable() {
        // Given
        String testObject = "test";

        // When
        String json = jsonUtils.toJson(testObject);
        String result = jsonUtils.fromJson(json, String.class);

        // Then
        assertThat(jsonUtils).isNotNull();
        assertThat(json).isEqualTo("\"test\"");
        assertThat(result).isEqualTo("test");
        assertThat(jsonUtils.isValidJson(json)).isTrue();
        assertThat(jsonUtils.isValidJson("invalid json")).isFalse();
    }

    @Test
    void restClientPropertiesHaveCorrectEndpoints() {
        // Then
        assertThat(restClientProperties.getLoginEndpoint()).isEqualTo("/api/phygital/capp/eassso/login");
        assertThat(restClientProperties.getPatientProfileEndpoint()).isEqualTo("/api/phygital/capp/easweb/lims/uf/patientProfile.do");
        assertThat(restClientProperties.getMaxRetries()).isEqualTo(3);
    }
}
