package com.test.demo.service;

import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class HttpClientService {

    private final RestClient externalApiRestClient;

    public String postFormData(String endpoint, MultiValueMap<String, String> formData, HttpHeaders headers) {
        try {
            return externalApiRestClient.post()
                .uri(endpoint)
                .headers(h -> h.addAll(headers))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(formData != null ? formData : new LinkedMultiValueMap<>())
                .retrieve()
                .body(String.class);
        } catch (RestClientResponseException ex) {
            handleHttpErrors(ex);
            throw ex;
        }
    }

    public String getWithCookies(String endpoint, Map<String, String> cookies, Map<String, String> queryParams) {
        try {
            return externalApiRestClient.get()
                .uri(builder -> {
                    var b = builder.path(endpoint);
                    if (queryParams != null) {
                        queryParams.forEach(b::queryParam);
                    }
                    return b.build();
                })
                .headers(h -> h.addAll(buildHeaders(cookies)))
                .retrieve()
                .body(String.class);
        } catch (RestClientResponseException ex) {
            handleHttpErrors(ex);
            throw ex;
        }
    }

    public HttpHeaders buildHeaders(Map<String, String> cookies) {
        HttpHeaders headers = new HttpHeaders();
        if (cookies != null && !cookies.isEmpty()) {
            StringBuilder cookieHeader = new StringBuilder();
            cookies.forEach((k, v) -> cookieHeader.append(k).append("=").append(v).append("; "));
            headers.add(HttpHeaders.COOKIE, cookieHeader.toString());
        }
        return headers;
    }

    private void handleHttpErrors(RestClientResponseException ex) {
        log.error("External API error: status={} body={}", ex.getStatusCode(), ex.getResponseBodyAsString());
    }
}


