package com.openclassrooms.SafetyNet.dto;


import lombok.Data;

import java.util.List;

@Data
public class PersonWithMedicalRecord {

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
