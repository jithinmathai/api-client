package com.test.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import com.test.demo.util.JsonUtils;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class RestClientConfig {

    private final RestClientProperties restClientProperties;
    private final JsonUtils jsonUtils;

    @Bean
    public RestClient externalApiRestClient() {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(restClientProperties.getConnectionTimeout());
        requestFactory.setReadTimeout(restClientProperties.getTimeout());

        return RestClient.builder()
            .baseUrl(restClientProperties.getBaseUrl())
            .defaultHeader(HttpHeaders.USER_AGENT, restClientProperties.getUserAgent())
            .defaultHeader(HttpHeaders.CONNECTION, restClientProperties.getConnection())
            .requestFactory(requestFactory)
            .build();
    }

    // Note: HttpServiceProxyFactory with RestClient has compatibility issues in Spring Boot 3.4.5
    // Using direct RestClient approach in service layer for now
    // TODO: Investigate HttpServiceProxyFactory compatibility in future Spring versions
}
