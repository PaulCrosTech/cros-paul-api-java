package com.openclassrooms.SafetyNet.utils;


import com.openclassrooms.SafetyNet.model.Firestation;
import com.openclassrooms.SafetyNet.model.MedicalRecord;
import com.openclassrooms.SafetyNet.model.Person;
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
