package com.test.demo.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginRequest {
    @NotBlank
    private String username;
    @NotBlank
    private String password;
    private String dataCenterCapp = "capp";
    private String locale = "1";
    private String ltrmlal = "1";
    private String flowExecutionKey = "e1s1";
    private String eventId = "submit";
    private String isPureWeb = "true";
    private String solutionName = "eas";
    private String dbType = "2";
    private String userAuthPattern = "BaseAD";
    private String loginFlow = "true";
    private String clientHostIP = "127.0.0.1";
}


