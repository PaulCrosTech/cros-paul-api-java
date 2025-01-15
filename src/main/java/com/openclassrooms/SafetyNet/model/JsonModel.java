package com.openclassrooms.SafetyNet.model;


import lombok.Data;

import java.util.List;

/**
 * JsonModel Class
 */
@Data
public class JsonModel {
    private List<Person> persons;
    private List<Firestation> firestations;
    private List<MedicalRecord> medicalrecords;
}
