package com.openclassrooms.SafetyNet.repository;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.SafetyNet.config.CustomProperties;
import com.openclassrooms.SafetyNet.model.Firestation;
import com.openclassrooms.SafetyNet.utils.JsonModel;
import com.openclassrooms.SafetyNet.model.MedicalRecord;
import com.openclassrooms.SafetyNet.model.Person;
import com.openclassrooms.SafetyNet.utils.JsonFileManager;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Tests for JsonFileManager
 */
@Log4j2
@ExtendWith(MockitoExtension.class)
public class JsonFileManagerTest {

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private CustomProperties customProperties;

    private JsonFileManager jsonFileManager;
    private JsonModel jsonModel;

    /**
     * Set up before each test
     */
    @BeforeEach
    public void setUpPerTest() throws IOException {
        jsonModel = new JsonModel();
        jsonModel.setPersons(List.of(
                new Person("John", "Doe", "address1", "city1", "zip1", "phone1", "email1@mail.com")
        ));
        jsonModel.setFirestations(List.of(
                new Firestation("address1", 1)
        ));
        jsonModel.setMedicalrecords(List.of(
                new MedicalRecord("John", "Doe", "01/01/2000", List.of("medication1"), List.of("allergy1"))
        ));

        when(customProperties.getJsonFilePath()).thenReturn("src/test/java/com/openclassrooms/SafetyNet/ressources/datas_for_tests.json");
        try {
            when(objectMapper.readValue(any(File.class), eq(JsonModel.class))).thenReturn(jsonModel);
        } catch (IOException e) {
            fail("Mock setup failed");
        }

        jsonFileManager = new JsonFileManager(objectMapper, customProperties);


    }


    /**
     * Test of getPersons method
     * - Given a JsonModel
     * - Then return list of Persons
     */
    @Test
    public void givenJsonModel_whenGetPersons_thenReturnListOfPerson() {
        // Given

        // When
        List<Person> persons = jsonFileManager.getPersons();

        // Then
        assertEquals(jsonModel.getPersons(), persons);
    }

    /**
     * Test of getFirestations method
     * - Given a JsonModel
     * - Then return list of Firestations
     */
    @Test
    public void givenJsonModel_whenGetFirestations_thenReturnListOfFirestations() {
        // Given

        // When
        List<Firestation> firestations = jsonFileManager.getFirestations();

        // Then
        assertEquals(jsonModel.getFirestations(), firestations);
    }

    /**
     * Test of getMedicalRecords method
     * - Given a JsonModel
     * - Then return list of MedicalRecords
     */
    @Test
    public void givenJsonModel_whenGetMedicalRecords_thenReturnListOfMedicalRecord() {
        // Given

        // When
        List<MedicalRecord> medicalRecords = jsonFileManager.getMedicalRecords();

        // Then
        assertEquals(jsonModel.getMedicalrecords(), medicalRecords);
    }


}
