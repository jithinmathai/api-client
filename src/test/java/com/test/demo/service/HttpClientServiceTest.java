package com.test.demo.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;

public class HttpClientServiceTest {

    private MockWebServer server;
    private HttpClientService clientService;

    @BeforeEach
    void setUp() throws Exception {
        server = new MockWebServer();
        server.start();
        RestClient restClient = RestClient.builder()
            .baseUrl(server.url("/").toString())
            .build();
        clientService = new HttpClientService(restClient);
    }

    @AfterEach
    void tearDown() throws Exception {
        server.shutdown();
    }

    @Test
    void postFormData_success() throws Exception {
        server.enqueue(new MockResponse().setResponseCode(200).setBody("OK").setHeader("Content-Type", MediaType.TEXT_PLAIN_VALUE));
        String body = clientService.postFormData("/login", null, new HttpHeaders());
        assertThat(body).isEqualTo("OK");
        RecordedRequest req = server.takeRequest();
        assertThat(req.getMethod()).isEqualTo("POST");
        assertThat(req.getPath()).isEqualTo("/login");
    }

    @Test
    void getWithCookies_success() throws Exception {
        server.enqueue(new MockResponse().setResponseCode(200).setBody("PROFILE").setHeader("Content-Type", MediaType.TEXT_PLAIN_VALUE));
        Map<String, String> cookies = new HashMap<>();
        cookies.put("SESSION", "abc");
        String body = clientService.getWithCookies("/profile", cookies, Map.of("method", "check"));
        assertThat(body).isEqualTo("PROFILE");
        RecordedRequest req = server.takeRequest();
        assertThat(req.getMethod()).isEqualTo("GET");
        assertThat(req.getPath()).isEqualTo("/profile?method=check");
        assertThat(req.getHeader("Cookie")).contains("SESSION=abc");
    }

    @Test
    void postFormData_error() {
        server.enqueue(new MockResponse().setResponseCode(400).setBody("Bad Request"));
        assertThrows(RestClientResponseException.class, () -> clientService.postFormData("/login", null, new HttpHeaders()));
    }

    @Test
    void getWithCookies_error() {
        server.enqueue(new MockResponse().setResponseCode(500).setBody("Oops"));
        assertThrows(RestClientResponseException.class, () -> clientService.getWithCookies("/p", null, null));
    }

    @Test
    void buildHeaders_withCookies_setsCookieHeader() {
        Map<String, String> cookies = new HashMap<>();
        cookies.put("A", "1");
        cookies.put("B", "2");
        HttpHeaders headers = clientService.buildHeaders(cookies);
        assertThat(headers.getFirst("Cookie")).contains("A=1").contains("B=2");
    }

    @Test
    void buildHeaders_nullCookies_returnsEmptyHeaders() {
        HttpHeaders headers = clientService.buildHeaders(null);
        assertThat(headers.containsKey("Cookie")).isFalse();
    }
}


