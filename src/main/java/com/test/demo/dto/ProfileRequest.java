package com.test.demo.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProfileRequest {
    
    // Login fields
    private String username;
    private String password;
    private String dataCenterCapp;
    private String loginFlow;
    private String clientHostIP;
    
    // Profile creation fields
    private String hkid;
    private String passportNo;
    private String lastname;
    private String firstname;
    private String gender;
    private String dob;
    private String contactNo;
    private String address;
    private String email;
    private String language;
    private String isBlue;
    private String optOut;
    
    // Additional fields for different request types
    private String method;
    private String sessionId;
    private String token;
}
