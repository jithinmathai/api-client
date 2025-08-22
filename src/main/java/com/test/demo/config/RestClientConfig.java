package com.test.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

import com.test.demo.client.QcareRestService;
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

    @Bean
    public QcareRestService qcareRestService(RestClient externalApiRestClient) {
        RestClientAdapter adapter = RestClientAdapter.create(externalApiRestClient);
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(adapter).build();
        return factory.createClient(QcareRestService.class);
    }
}
