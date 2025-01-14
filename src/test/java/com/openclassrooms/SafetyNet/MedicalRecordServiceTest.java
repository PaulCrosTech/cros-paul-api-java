package com.openclassrooms.SafetyNet;


import com.openclassrooms.SafetyNet.exceptions.ConflictException;
import com.openclassrooms.SafetyNet.exceptions.JsonFileManagerSaveException;
import com.openclassrooms.SafetyNet.exceptions.NotFoundException;
import com.openclassrooms.SafetyNet.model.MedicalRecord;
import com.openclassrooms.SafetyNet.repository.MedicalRecordRepository;
import com.openclassrooms.SafetyNet.service.MedicalRecordService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class MedicalRecordServiceTest {

    private static MedicalRecordService medicalRecordService;

    @Mock
    private MedicalRecordRepository medicalRecordRepository;

    private List<MedicalRecord> medicalRecords;

    @BeforeEach
    public void setUp() {
        medicalRecordService = new MedicalRecordService(medicalRecordRepository);
        medicalRecords = Arrays.asList(
                new MedicalRecord("John", "Boyd", "03/06/1984", Arrays.asList("aznol:350mg", "hydrapermazol:100mg"), List.of("nillacilan")),
                new MedicalRecord("Jacob", "Boyd", "03/06/1989", Arrays.asList("pharmacol:5000mg", "terazine:10mg", "noznazol:250mg"), List.of()),
                new MedicalRecord("Tenley", "Boyd", "02/18/2012", List.of(), List.of("peanut"))
        );
    }


    /**
     * Testing getMedicalRecords() method from MedicalRecordService
     */
    @Test
    public void givenNothing_whenGetMedicalRecords_thenReturnMedicalRecordList() {

        // Given
        when(medicalRecordRepository.getMedicalRecords()).thenReturn(medicalRecords);

        // When
        List<MedicalRecord> medicalRecordList = medicalRecordService.getMedicalRecords();

        // Then
        assertEquals(medicalRecords, medicalRecordList);
    }

    /**
     * Testing getMedicalRecordByFirstNameAndLastName() method from MedicalRecordService
     */
    @Test
    public void givenExistingName_whenGetMedicalRecordByFirstNameAndLastName_thenReturnMedicalRecord() {

        // Given
        MedicalRecord medicalRecordExpected = medicalRecords.getFirst();

        when(medicalRecordRepository
                .getMedicalRecordByFirstNameAndLastName(medicalRecordExpected.getFirstName(), medicalRecordExpected.getLastName()))
                .thenReturn(medicalRecordExpected);

        // When
        MedicalRecord medicalRecord = medicalRecordService
                .getMedicalRecordByFirstNameAndLastName(medicalRecordExpected.getFirstName(), medicalRecordExpected.getLastName());

        // Then
        assertEquals(medicalRecordExpected, medicalRecord);
    }

    /**
     * Testing getMedicalRecordByFirstNameAndLastName() method from MedicalRecordService
     */
    @Test
    public void givenNonExistingName_whenGetMedicalRecordByFirstNameAndLastName_thenThrowNotFoundException() {
        // Given
        String firstName = "NonExistingFirstName";
        String lastName = "NonExistingLastName";
        when(medicalRecordRepository
                .getMedicalRecordByFirstNameAndLastName(firstName, lastName))
                .thenReturn(null);

        // When && Then
        assertThrows(NotFoundException.class,
                () -> medicalRecordService.getMedicalRecordByFirstNameAndLastName(firstName, lastName));
    }

    /**
     * Testing deleteMedicalRecordByFirstNameAndLastName() method from MedicalRecordService
     *
     * @throws Exception Exception
     */
    @Test
    public void givenExistingMedicalRecord_whenDeleteMedicalRecordByFirstNameAndLastName_thenMedicalRecordDeleted() throws Exception {
        // Given
        MedicalRecord medicalRecordExpected = medicalRecords.getFirst();
        when(medicalRecordRepository
                .deleteMedicalRecordByFirstNameAndLastName(medicalRecordExpected.getFirstName(), medicalRecordExpected.getLastName()))
                .thenReturn(true);

        // When
        medicalRecordService.deleteMedicalRecordByFirstNameAndLastName(medicalRecordExpected.getFirstName(), medicalRecordExpected.getLastName());

        // Then
        verify(medicalRecordRepository, times(1))
                .deleteMedicalRecordByFirstNameAndLastName(medicalRecordExpected.getFirstName(), medicalRecordExpected.getLastName());
    }

    /**
     * Testing deleteMedicalRecordByFirstNameAndLastName() method from MedicalRecordService
     */
    @Test
    public void givenNonExistingMedicalRecord_whenDeleteMedicalRecordByFirstNameAndLastName_thenThrowNotFoundException() {
        // Given
        String firstName = "NonExistingFirstName";
        String lastName = "NonExistingLastName";
        when(medicalRecordRepository
                .deleteMedicalRecordByFirstNameAndLastName(firstName, lastName))
                .thenReturn(false);

        // When && Then
        assertThrows(NotFoundException.class,
                () -> medicalRecordService.deleteMedicalRecordByFirstNameAndLastName(firstName, lastName));
    }

    /**
     * Testing deleteMedicalRecordByFirstNameAndLastName() method from MedicalRecordService
     */
    @Test
    public void givenExistingMedicalRecord_whenDeleteMedicalRecordByFirstNameAndLastName_thenThrowException() {
        // Given
        MedicalRecord medicalRecordExpected = medicalRecords.getFirst();

        when(medicalRecordRepository
                .deleteMedicalRecordByFirstNameAndLastName(medicalRecordExpected.getFirstName(), medicalRecordExpected.getLastName()))
                .thenThrow(new JsonFileManagerSaveException("Error while deleting the medical record in JSON file, firstName: " + medicalRecordExpected.getFirstName() + " and lastName: " + medicalRecordExpected.getLastName()));

        // When && Then
        assertThrows(Exception.class,
                () -> medicalRecordService.deleteMedicalRecordByFirstNameAndLastName(medicalRecordExpected.getFirstName(), medicalRecordExpected.getLastName()));
    }

    /**
     * Testing saveMedicalRecord() method from MedicalRecordService
     */
    @Test
    public void givenNewMedicalRecord_whenSaveMedicalRecord_thenMedicalRecordSaved() {
        // Given
        MedicalRecord medicalRecordExpected = new MedicalRecord("NewFirstName", "NewLastName", "01/01/2000", List.of("med1:100mg"), List.of("allergy1"));
        when(medicalRecordRepository
                .getMedicalRecordByFirstNameAndLastName(medicalRecordExpected.getFirstName(), medicalRecordExpected.getLastName()))
                .thenReturn(null);

        // When
        medicalRecordService.saveMedicalRecord(medicalRecordExpected);

        // Then
        verify(medicalRecordRepository, times(1)).saveMedicalRecord(medicalRecordExpected);
    }

    /**
     * Testing saveMedicalRecord() method from MedicalRecordService
     */
    @Test
    public void givenExistingMedicalRecord_whenSaveMedicalRecord_thenThrowConflictException() {
        // Given
        MedicalRecord medicalRecordExpected = medicalRecords.getFirst();
        when(medicalRecordRepository
                .getMedicalRecordByFirstNameAndLastName(medicalRecordExpected.getFirstName(), medicalRecordExpected.getLastName()))
                .thenReturn(medicalRecordExpected);

        // When && Then
        assertThrows(ConflictException.class,
                () -> medicalRecordService.saveMedicalRecord(medicalRecordExpected));
    }

    /**
     * Testing saveMedicalRecord() method from MedicalRecordService
     */
    @Test
    public void givenNewMedicalRecord_whenSaveMedicalRecord_thenThrowJsonFileManagerSaveException() {
        // Given
        MedicalRecord medicalRecordExpected = new MedicalRecord("NewFirstName", "NewLastName", "01/01/2000", List.of("med1:100mg"), List.of("allergy1"));
        when(medicalRecordRepository
                .getMedicalRecordByFirstNameAndLastName(medicalRecordExpected.getFirstName(), medicalRecordExpected.getLastName()))
                .thenReturn(null);

        doThrow(new JsonFileManagerSaveException("Error while saving the medical record in JSON file, firstName: " + medicalRecordExpected.getFirstName() + " and lastName: " + medicalRecordExpected.getLastName()))
                .when(medicalRecordRepository)
                .saveMedicalRecord(medicalRecordExpected);

        // When && Then
        assertThrows(JsonFileManagerSaveException.class,
                () -> medicalRecordService.saveMedicalRecord(medicalRecordExpected));
    }

    /**
     * Testing updateMedicalRecord() method from MedicalRecordService
     */
    @Test
    public void givenExistingMedicalRecord_whenUpdateMedicalRecord_thenReturnMedicalRecordUpdated() {
        // Given
        MedicalRecord medicalRecordExpected = medicalRecords.getFirst();
        medicalRecordExpected.setBirthdate("01/01/2000");

        when(medicalRecordRepository
                .updateMedicalRecord(medicalRecordExpected))
                .thenReturn(medicalRecordExpected);

        // When
        MedicalRecord medicalRecord = medicalRecordService.updateMedicalRecord(medicalRecordExpected);

        // Then
        assertEquals(medicalRecordExpected, medicalRecord);
    }

    /**
     * Testing updateMedicalRecord() method from MedicalRecordService
     */
    @Test
    public void givenNonExistingMedicalRecord_whenUpdateMedicalRecord_thenThrowNotFoundException() {
        // Given
        MedicalRecord medicalRecordExpected = new MedicalRecord("UnknowFirstName", "UnknowLastName", "03/06/1984", Arrays.asList("aznol:350mg", "hydrapermazol:100mg"), List.of("nillacilan"));

        when(medicalRecordRepository
                .updateMedicalRecord(medicalRecordExpected))
                .thenReturn(null);

        // When && Then
        assertThrows(NotFoundException.class,
                () -> medicalRecordService.updateMedicalRecord(medicalRecordExpected));
    }
}
