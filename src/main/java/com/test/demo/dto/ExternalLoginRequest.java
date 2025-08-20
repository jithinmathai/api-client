package com.test.demo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExternalLoginRequest {
    private String locale;
    private String dataCenterCapp;
    private String username;
    private String password;
    private String ltrmlal;
    @JsonProperty("_flowExecutionKey")
    private String flowExecutionKey;
    @JsonProperty("_eventId")
    private String eventId;
    private String isPureWeb;
    private String solutionName;
    private String dbType;
    private String userAuthPattern;
    private String loginFlow;
    private String clientHostIP;
}


