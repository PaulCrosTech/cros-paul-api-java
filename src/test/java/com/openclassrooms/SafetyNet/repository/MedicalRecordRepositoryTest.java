package com.openclassrooms.SafetyNet.repository;


import com.openclassrooms.SafetyNet.model.MedicalRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests for MedicalRecordRepository
 */
@ExtendWith(MockitoExtension.class)
public class MedicalRecordRepositoryTest {

    private static MedicalRecordRepository medicalRecordRepository;

    @Mock
    private JsonFileManager jsonFileManager;

    private List<MedicalRecord> medicalRecords;

    /**
     * Set up before each test
     */
    @BeforeEach
    public void setUpPerTest() {
        medicalRecordRepository = new MedicalRecordRepository(jsonFileManager);
        medicalRecords = new ArrayList<>(Arrays.asList(
                new MedicalRecord("John", "Boyd", "03/06/1984", Arrays.asList("aznol:350mg", "hydrapermazol:100mg"), List.of("nillacilan")),
                new MedicalRecord("Jacob", "Boyd", "03/06/1989", Arrays.asList("pharmacol:5000mg", "terazine:10mg", "noznazol:250mg"), List.of()),
                new MedicalRecord("Tenley", "Boyd", "02/18/2012", List.of(), List.of("peanut"))
        ));
    }

    /**
     * Testing method getMedicalRecords
     * - Given medicalRecords list
     * - Then medicalRecords list
     */
    @Test
    public void givenMedicalRecordList_whenGetMedicalRecords_thenReturnMedicalRecordList() {

        // Given
        when(jsonFileManager.getMedicalRecords()).thenReturn(medicalRecords);

        // When
        List<MedicalRecord> medicalRecordList = medicalRecordRepository.getMedicalRecords();

        // Then
        assertEquals(medicalRecords, medicalRecordList);
    }

    /**
     * Testing method getMedicalRecordByFirstNameAndLastName
     * - Given medicalRecords list
     * - Then medicalRecord
     */
    @Test
    public void givenExistingName_whenGetMedicalRecordByFirstNameAndLastName_thenReturnMedicalRecord() {

        // Given
        MedicalRecord mdExpected = medicalRecords.getFirst();
        when(jsonFileManager.getMedicalRecords()).thenReturn(medicalRecords);

        // When
        MedicalRecord medicalRecord = medicalRecordRepository.getMedicalRecordByFirstNameAndLastName(mdExpected.getFirstName(), mdExpected.getLastName());

        // Then
        assertEquals(mdExpected, medicalRecord);
    }


    /**
     * Testing method getBirthdateByFirstNameAndLastName
     * - Given existing name
     * - Then return birthdate
     */
    @Test
    public void givenExistingName_whenGetBirthdateByFirstNameAndLastName_thenReturnBirthdate() {
        // Given
        MedicalRecord mdExpected = medicalRecords.getFirst();
        when(jsonFileManager.getMedicalRecords()).thenReturn(medicalRecords);

        // When
        String birthdate = medicalRecordRepository.getBirthdateByFirstNameAndLastName("John", "Boyd");

        // Then
        assertEquals("03/06/1984", birthdate);
    }

    /**
     * Testing method getBirthdateByFirstNameAndLastName
     * - Given non-existing name
     * - Then return null
     */
    @Test
    public void givenNonExistingName_whenGetBirthdateByFirstNameAndLastName_thenReturnNull() {
        // Given
        when(jsonFileManager.getMedicalRecords()).thenReturn(medicalRecords);

        // When
        String birthdate = medicalRecordRepository.getBirthdateByFirstNameAndLastName("UnknowFirstName", "UnknowLastName");

        // Then
        assertNull(birthdate);
    }

    /**
     * Testing method deleteMedicalRecordByFirstNameAndLastName
     * - Given existing name
     * - Then delete medicalRecord
     */
    @Test
    public void givenExistingName_whenDeleteMedicalRecordByFirstNameAndLastName_thenDeleteMedicalRecord() {

        // Given
        MedicalRecord mdExpected = medicalRecords.getFirst();
        when(jsonFileManager.getMedicalRecords()).thenReturn(medicalRecords);

        // When
        boolean deleted = medicalRecordRepository.deleteMedicalRecordByFirstNameAndLastName(
                mdExpected.getFirstName(), mdExpected.getLastName()
        );

        // Then
        assertTrue(deleted);
        verify(jsonFileManager, times(1)).saveJsonFile();
        assertFalse(medicalRecords.contains(mdExpected));
    }

    /**
     * Testing method deleteMedicalRecordByFirstNameAndLastName
     * - Given non-existing name
     * - Then return false
     */
    @Test
    public void givenNonExistingName_whenDeleteMedicalRecordByFirstNameAndLastName_thenReturnFalse() {

        // Given
        when(jsonFileManager.getMedicalRecords()).thenReturn(medicalRecords);

        // When
        boolean deleted = medicalRecordRepository.deleteMedicalRecordByFirstNameAndLastName("UnknowFirstName", "UnknowLastName");

        // Then
        assertFalse(deleted);
        verify(jsonFileManager, times(0)).saveJsonFile();
    }


    /**
     * Testing method saveMedicalRecord
     * - Given new medicalRecord
     * - Then save medicalRecord
     */
    @Test
    public void givenNewMedicalRecord_whenSaveMedicalRecord_thenSaveMedicalRecord() {

        // Given
        MedicalRecord newMedicalRecord = new MedicalRecord("NewFirstName", "NewLastName", "01/01/2000", List.of("med1:100mg"), List.of("allergy"));
        when(jsonFileManager.getMedicalRecords()).thenReturn(medicalRecords);

        // When
        medicalRecordRepository.saveMedicalRecord(newMedicalRecord);

        // Then
        verify(jsonFileManager, times(1)).saveJsonFile();
        assertTrue(medicalRecords.contains(newMedicalRecord));
    }

    /**
     * Testing  method saveMedicalRecord
     * - Given existing medicalRecord
     * - Then medical record is updated
     */
    @Test
    public void givenExistingMedicalRecord_whenUpdateMedicalRecord_thenReturnMedicalRecordUpdated() {
        // Given
        MedicalRecord mdExpected = medicalRecords.getFirst();
        mdExpected.setBirthdate("01/01/2000");
        when(jsonFileManager.getMedicalRecords()).thenReturn(medicalRecords);

        // When
        MedicalRecord medicalRecord = medicalRecordRepository.updateMedicalRecord(mdExpected);

        // Then
        assertEquals(mdExpected, medicalRecord);
        verify(jsonFileManager, times(1)).saveJsonFile();
        assertTrue(medicalRecords.contains(mdExpected));
    }

    /**
     * Testing method saveMedicalRecord
     * - Given non-existing medicalRecord
     * - Then return null
     */
    @Test
    public void givenNonExistingMedicalRecord_whenUpdateMedicalRecord_thenReturnNull() {
        // Given
        MedicalRecord mdExpected = new MedicalRecord("UnknowFirstName", "UnknowLastName", "01/01/2000", List.of("med1:100mg"), List.of("allergy"));
        when(jsonFileManager.getMedicalRecords()).thenReturn(medicalRecords);

        // When
        MedicalRecord medicalRecord = medicalRecordRepository.updateMedicalRecord(mdExpected);

        // Then
        assertNull(medicalRecord);
        verify(jsonFileManager, times(0)).saveJsonFile();
        assertFalse(medicalRecords.contains(mdExpected));
    }

}
