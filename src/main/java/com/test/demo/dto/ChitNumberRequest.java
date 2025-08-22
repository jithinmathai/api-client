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
public class ChitNumberRequest {
    
    private String sessionId;
    private String token;
    private String hkid;
    private String profileId;
    private String serviceType;
    private String priority;
    private String department;
    private String doctorId;
    private String appointmentDate;
    private String appointmentTime;
    private String notes;
    private String requestedBy;
}
