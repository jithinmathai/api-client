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
public class ChitNumberResponse {
    
    private String chitNumber;
    private String chitId;
    private String status;
    private String queueNumber;
    private String estimatedWaitTime;
    private String department;
    private String serviceType;
    private String appointmentDate;
    private String appointmentTime;
    private String doctorName;
    private String doctorId;
    private String createdDate;
    private String message;
    private String priority;
}
