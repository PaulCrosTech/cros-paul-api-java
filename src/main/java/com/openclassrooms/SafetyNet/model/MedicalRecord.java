package com.openclassrooms.SafetyNet.model;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class MedicalRecord {
    private String firstName;
    private String lastName;
    private LocalDate birthdate;
    private List<String> medications;
    private List<String> allergies;
}
