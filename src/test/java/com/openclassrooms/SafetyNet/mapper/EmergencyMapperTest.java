package com.openclassrooms.SafetyNet.mapper;

import com.openclassrooms.SafetyNet.dto.*;
import com.openclassrooms.SafetyNet.model.Firestation;
import com.openclassrooms.SafetyNet.model.MedicalRecord;
import com.openclassrooms.SafetyNet.model.Person;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;


/**
 * Tests for EmergencyMapper class
 */
@ExtendWith(MockitoExtension.class)
public class EmergencyMapperTest {

    private static EmergencyMapper emergencyMapper;


    private List<Person> persons;
    private List<MedicalRecord> medicalRecords;
    private Firestation firestation;
    private HashMap<String, String> birthdays;

    /**
     * Set up before each test
     */
    @BeforeEach
    public void setUpPerTest() {
        emergencyMapper = new EmergencyMapper();
        // Firestation
        firestation = new Firestation("1509 Culver St", 1);
        // Persons
        persons = Arrays.asList(
                new Person("John", "Boyd", "1509 Culver St", "Culver", "97451", "841-874-6512", "jaboyd@email.com"),
                new Person("Jacob", "Boyd", "1509 Culver St", "Culver", "97451", "841-874-6513", "drk@email.com")
        );
        // Medical records
        // Child and adult birthdate
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        String childBirthDate = LocalDate.now().minusYears(5).format(formatter);
        String adultBirthDate = LocalDate.now().minusYears(30).format(formatter);
        medicalRecords = Arrays.asList(
                new MedicalRecord("John", "Boyd", adultBirthDate, Arrays.asList("aznol:350mg", "hydrapermazol:100mg"), List.of("nillacilan")),
                new MedicalRecord("Jacob", "Boyd", childBirthDate, Arrays.asList("pharmacol:5000mg", "terazine:10mg", "noznazol:250mg"), List.of())
        );

    }

    /**
     * Testing method toPersonCoveredByStationDTO
     * - Given list fo persons and list of medical records
     * - Then return PersonCoveredByStationDTO
     */
    @Test
    public void givenPersonsMedicalRecords_whenToPersonCoveredByStationDTO_thenReturnPersonCoveredByStationDTO() {
        // Given
        PersonBasicDetailsDTO p1Expected = new PersonBasicDetailsDTO("John", "Boyd", "1509 Culver St", "841-874-6512");
        PersonBasicDetailsDTO p2Expected = new PersonBasicDetailsDTO("Jacob", "Boyd", "1509 Culver St", "841-874-6513");

        Map<Person, String> personsWithBirthdate = new LinkedHashMap<>();
        personsWithBirthdate.put(persons.get(0), medicalRecords.get(0).getBirthdate());
        personsWithBirthdate.put(persons.get(1), medicalRecords.get(1).getBirthdate());


        // When
        PersonCoveredByStationDTO personCoveredByStationDTO = emergencyMapper.toPersonCoveredByStationDTO(personsWithBirthdate);

        // Then
        assertEquals(1, personCoveredByStationDTO.getNbChildren());
        assertEquals(1, personCoveredByStationDTO.getNbAdults());
        assertEquals(2, personCoveredByStationDTO.getPersons().size());

        PersonBasicDetailsDTO p1 = personCoveredByStationDTO.getPersons().get(0);
        assertEquals(p1Expected, p1);

        PersonBasicDetailsDTO p2 = personCoveredByStationDTO.getPersons().get(1);
        assertEquals(p2Expected, p2);
    }

    /**
     * Testing method toHouseChildrenDTO
     * - Given list of persons with birthdate
     * - Then return List of HouseChildrenDTO
     */
    @Test
    public void givenListOfPersonWithBirthdate_whenToHouseChildrenDTO_thenReturnListOfHouseChildrenDTO() {
        // Given
        Map<Person, String> personWithBirthdate = new LinkedHashMap<>();
        personWithBirthdate.put(persons.get(0), medicalRecords.get(0).getBirthdate());
        personWithBirthdate.put(persons.get(1), medicalRecords.get(1).getBirthdate());

        // When
        List<HouseChildrenDTO> houseChildrenDTOList = emergencyMapper.toHouseChildrenDTO(personWithBirthdate);

        // Then
        assertEquals(1, houseChildrenDTOList.size());
        assertEquals("Jacob", houseChildrenDTOList.getFirst().getFirstName());
        assertEquals(1, houseChildrenDTOList.getFirst().getHouseMembersDTO().size());
        assertEquals("John", houseChildrenDTOList.getFirst().getHouseMembersDTO().getFirst().getFirstName());
    }


    /**
     * Testing method toFamilyWithMedicalAndFirestationDTO
     * - Given list of persons, list of medical records and a firestation
     * - Then return FamilyWithMedicalAndFirestationDTO
     */
    @Test
    public void givenPersonsMedicalRecordsFirestation_whenToFamilyWithMedicalAndFirestationDTO_thenRetrunFamilyWithMedicalAndFirestationDTO() {

        // Given
        FamilyWithMedicalAndFirestationDTO familyExpected = new FamilyWithMedicalAndFirestationDTO(1, List.of(
                new PersonMedicalDetailsDTO("John", "Boyd", 30, "841-874-6512", List.of("aznol:350mg", "hydrapermazol:100mg"), List.of("nillacilan")),
                new PersonMedicalDetailsDTO("Jacob", "Boyd", 5, "841-874-6513", List.of("pharmacol:5000mg", "terazine:10mg", "noznazol:250mg"), List.of())
        ));

        // When
        FamilyWithMedicalAndFirestationDTO familyWithMedicalAndFirestationDTO =
                emergencyMapper.toFamilyWithMedicalAndFirestationDTO(persons, medicalRecords, firestation);

        // Then
        assertEquals(familyExpected, familyWithMedicalAndFirestationDTO);

    }

    /**
     * Testing method toPersonWithMedicalAndPhoneDTO
     * - Given PersonWithMedicalRecordDTO
     * - Then return PersonWithMedicalAndPhoneDTO
     */
    @Test
    public void givenPersonWithMedicalRecordDTO_WhenToPersonWithMedicalAndPhone_thenPersonWithMedicalAndPhoneDTO() {
        // Given
        PersonWithMedicalRecordDTO personWithMedicalRecordDTO =
                new PersonWithMedicalRecordDTO("John", "Boyd", "1509 Culver St", "Culver", "97451", "841-874-6512", "jaboyd@email.com",
                        30, true, Arrays.asList("aznol:350mg", "hydrapermazol:100mg"), List.of("nillacilan"));

        PersonWithMedicalAndPhoneDTO personExpected = new PersonWithMedicalAndPhoneDTO("John", "Boyd", 30, "841-874-6512",
                Arrays.asList("aznol:350mg", "hydrapermazol:100mg"), List.of("nillacilan"));
        // When
        PersonWithMedicalAndPhoneDTO personWithMedicalAndPhoneDTO = emergencyMapper.toPersonWithMedicalAndPhone(personWithMedicalRecordDTO);

        // Then
        assertEquals(personExpected, personWithMedicalAndPhoneDTO);
    }

    /**
     * Testing method toPersonWithMedicalAndEmailDTO
     * - Given PersonWithMedicalRecordDTO
     * - Then return PersonWithMedicalAndEmailDTO
     */
    @Test
    public void givenPersonWithMedicalRecordDTO_WhenToPersonWithMedicalAndEmailDTO_thenPersonWithMedicalAndEmailDTO() {
        // Given
        PersonWithMedicalRecordDTO personWithMedicalRecordDTO =
                new PersonWithMedicalRecordDTO("John", "Boyd", "1509 Culver St", "Culver", "97451", "841-874-6512", "jaboyd@email.com",
                        30, true, Arrays.asList("aznol:350mg", "hydrapermazol:100mg"), List.of("nillacilan"));

        PersonWithMedicalAndEmailDTO personExpected = new PersonWithMedicalAndEmailDTO("John", "Boyd", 30, "jaboyd@email.com",
                Arrays.asList("aznol:350mg", "hydrapermazol:100mg"), List.of("nillacilan"));
        // When
        PersonWithMedicalAndEmailDTO personWithMedicalAndEmailDTO = emergencyMapper.toPersonWithMedicalAndEmailDTO(personWithMedicalRecordDTO);

        // Then
        assertEquals(personExpected, personWithMedicalAndEmailDTO);
    }
}
