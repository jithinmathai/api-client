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
public class ProfileResponse {
    
    // Authentication response fields
    private String token;
    private String sessionId;
    private String subscriptionKey;
    private Long expiresIn;
    private String message;
    
    // Profile data fields
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
    
    // Additional metadata
    private String status;
    private String profileId;
    private String createdDate;
    private String updatedDate;
}
