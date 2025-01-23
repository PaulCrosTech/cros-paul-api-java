package com.openclassrooms.SafetyNet.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * PersonWithMedicalRecordDTO Class
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PersonWithMedicalRecordDTO {

    private String firstName;
    private String lastName;
    private String address;
    private String city;
    private String zip;
    private String phone;
    private String email;

    private Integer age;
    private Boolean isAdult;

    private List<String> medications;
    private List<String> allergies;
}
