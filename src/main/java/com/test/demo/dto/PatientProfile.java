package com.test.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PatientProfile {
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
}


