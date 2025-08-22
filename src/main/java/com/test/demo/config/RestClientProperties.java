package com.test.demo.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
@ConfigurationProperties(prefix = "external.healthcare")
public class RestClientProperties {
    
    private String baseUrl;
    private String loginEndpoint;
    private String patientProfileEndpoint;
    private int timeout = 30000;
    private int maxRetries = 3;
    private int connectionTimeout = 10000;
    
    // Additional properties for RestClient configuration
    private String userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36";
    private String connection = "keep-alive";
}
