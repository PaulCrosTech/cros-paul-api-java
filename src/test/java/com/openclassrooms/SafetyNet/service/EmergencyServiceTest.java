package com.openclassrooms.SafetyNet.service;

import com.openclassrooms.SafetyNet.dto.*;
import com.openclassrooms.SafetyNet.exceptions.NotFoundException;
import com.openclassrooms.SafetyNet.mapper.EmergencyMapper;
import com.openclassrooms.SafetyNet.model.Firestation;
import com.openclassrooms.SafetyNet.model.MedicalRecord;
import com.openclassrooms.SafetyNet.model.Person;
import com.openclassrooms.SafetyNet.repository.FirestationRepository;
import com.openclassrooms.SafetyNet.repository.MedicalRecordRepository;
import com.openclassrooms.SafetyNet.repository.PersonRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests of EmergencyService
 */
@ExtendWith(MockitoExtension.class)
public class EmergencyServiceTest {

    private EmergencyService emergencyService;

    @Mock
    PersonRepository personRepository;
    @Mock
    FirestationRepository firestationRepository;
    @Mock
    MedicalRecordRepository medicalRecordRepository;
    @Mock
    EmergencyMapper emergencyMapper;

    /**
     * Set up before each test
     */
    @BeforeEach
    public void setUpPerTest() {
        emergencyService = new EmergencyService(personRepository, firestationRepository, medicalRecordRepository, emergencyMapper);
    }


    /**
     * Testing method getPersonCoveredByStationNumber
     * - Given existing station number
     * - Then PersonCoveredByStationDTO
     */
    @Test
    public void givenExistingStationNumber_whenGetPersonCoveredByStationNumber_thenReturnPersonCoveredByStationDTO() {
        // Given
        int stationNumber = 1;
        String address = "1509 Culver St";
        String firstName = "John";
        String lastName = "Boyd";
        String birthdate = "03/06/1984";

        List<Firestation> firestations = new ArrayList<>();
        firestations.add(new Firestation(address, stationNumber));

        List<Person> persons = new ArrayList<>();
        persons.add(new Person(firstName, lastName, address, "Culver", "97451", "841-874-6512", "jaboyd@email.com"));

        Map<Person, String> personWithBirthdate = new HashMap<>();
        personWithBirthdate.put(persons.getFirst(), birthdate);

        PersonCoveredByStationDTO expectedDTO = new PersonCoveredByStationDTO();

        when(firestationRepository.getFirestationByStationNumber(stationNumber)).thenReturn(firestations);
        when(personRepository.getPersonByAddress(address)).thenReturn(persons);
        when(medicalRecordRepository.getBirthdateByFirstNameAndLastName(firstName, lastName)).thenReturn(birthdate);
        when(emergencyMapper.toPersonCoveredByStationDTO(personWithBirthdate)).thenReturn(expectedDTO);

        // When
        PersonCoveredByStationDTO result = emergencyService.getPersonCoveredByStationNumber(stationNumber);

        // Then
        assertNotNull(result);
        assertEquals(expectedDTO.getPersons(), result.getPersons());
        assertEquals(expectedDTO.getNbAdults(), result.getNbAdults());
        assertEquals(expectedDTO.getNbChildren(), result.getNbChildren());

        verify(firestationRepository, times(1)).getFirestationByStationNumber(stationNumber);
        verify(personRepository, times(1)).getPersonByAddress(address);
        verify(medicalRecordRepository, times(1)).getBirthdateByFirstNameAndLastName(firstName, lastName);
        verify(emergencyMapper, times(1)).toPersonCoveredByStationDTO(personWithBirthdate);
    }

    /**
     * Testing method getPersonCoveredByStationNumber
     * - Given non-existing station number
     * - Then NotFoundException
     */
    @Test
    public void givenNonExistingStationNumber_whenGetPersonCoveredByStationNumber_thenReturnNotFoundException() {
        // Given
        List<Firestation> firestations = new ArrayList<>();

        when(firestationRepository.getFirestationByStationNumber(anyInt())).thenReturn(firestations);

        // When & Then
        assertThrows(NotFoundException.class, () -> emergencyService.getPersonCoveredByStationNumber(anyInt()));

        verify(firestationRepository, times(1)).getFirestationByStationNumber(anyInt());
        verify(personRepository, times(0)).getPersonByAddress(anyString());
        verify(medicalRecordRepository, times(0)).getMedicalRecordByFirstNameAndLastName(anyString(), anyString());
        verify(emergencyMapper, times(0)).toPersonCoveredByStationDTO(anyMap());
    }

    /**
     * Testing method getHouseChildren
     * - Given existing address
     * - Then return list of HouseChildrenDTO
     */
    @Test
    public void givenExistingAddress_whenGetHouseChildren_thenReturnListOfHouseChildrenDTO() {
        // Given
        String address = "1509 Culver St";

        List<Person> persons = new ArrayList<>();
        persons.add(new Person("John", "Boyd", address, "Culver", "97451", "841-874-6512", "jaboyd@email.com"));
        persons.add(new Person("Jacob", "Boyd", address, "Culver", "97451", "841-874-6513", "jacob@email.com"));

        when(personRepository.getPersonByAddress(address)).thenReturn(persons);

        List<MedicalRecord> medicalRecords = new ArrayList<>();
        medicalRecords.add(new MedicalRecord("John", "Boyd", "03/06/1984", new ArrayList<>(), new ArrayList<>()));
        medicalRecords.add(new MedicalRecord("Jacob", "Boyd", "03/06/2024", new ArrayList<>(), new ArrayList<>()));

        when(medicalRecordRepository.getBirthdateByFirstNameAndLastName("John", "Boyd")).thenReturn(medicalRecords.getFirst().getBirthdate());
        when(medicalRecordRepository.getBirthdateByFirstNameAndLastName("Jacob", "Boyd")).thenReturn(medicalRecords.get(1).getBirthdate());

        Map<Person, String> personWithBirthdate = new HashMap<>();
        personWithBirthdate.put(persons.get(0), medicalRecords.get(0).getBirthdate());
        personWithBirthdate.put(persons.get(1), medicalRecords.get(1).getBirthdate());

        List<HouseChildrenDTO> expectedDTOList = new ArrayList<>();
        expectedDTOList.add(new HouseChildrenDTO("Jacob", "Boyd", 3,
                        List.of(new HouseMemberDTO("John", "Boyd", 37))
                )
        );

        when(emergencyMapper.toHouseChildrenDTO(personWithBirthdate)).thenReturn(expectedDTOList);

        // When
        List<HouseChildrenDTO> houseChildrenDTOList = emergencyService.getHouseChildren(address);

        // Then
        assertEquals(expectedDTOList, houseChildrenDTOList);
        verify(personRepository, times(1)).getPersonByAddress(address);
        verify(medicalRecordRepository, times(1)).getBirthdateByFirstNameAndLastName("John", "Boyd");
    }


    /**
     * Testing method GetPhoneNumbersCoveredByFireStation
     * - Given existing station number
     * - Then phones numbers
     */
    @Test
    public void givenExistingStationNumber_givenGetPhoneNumbersCoveredByFireStation_thenReturnPhones() {
        // Given
        int stationNumber = 1;

        List<Firestation> firestations = new ArrayList<>();
        firestations.add(new Firestation("1509 Culver St", stationNumber));

        List<Person> persons = new ArrayList<>();
        persons.add(new Person("John", "Boyd", "1509 Culver St", "Culver", "97451", "841-874-6512", "jaboyd@email.com"));

        when(firestationRepository.getFirestationByStationNumber(stationNumber)).thenReturn(firestations);
        when(personRepository.getPersonByAddress("1509 Culver St")).thenReturn(persons);

        // When
        HashSet<String> phones = emergencyService.getPhoneNumbersCoveredByFireStation(stationNumber);

        // Then
        verify(firestationRepository, times(1)).getFirestationByStationNumber(stationNumber);
        verify(personRepository, times(1)).getPersonByAddress("1509 Culver St");
        assertEquals(1, phones.size());
        assertEquals("841-874-6512", phones.iterator().next());
    }

    /**
     * Testing method getPhoneNumbersCoveredByFireStation
     * - Given non-existing station number
     * - Then NotFoundException
     */
    @Test
    public void givenNonExistingStationNumber_givenGetPhoneNumbersCoveredByFireStation_thenThrowNotFoundException() {
        // Given
        when(firestationRepository.getFirestationByStationNumber(1)).thenReturn(new ArrayList<>());

        // When && Then
        assertThrows(NotFoundException.class, () -> emergencyService.getPhoneNumbersCoveredByFireStation(1));
    }


    /**
     * Testing method getFamilyWithMedicalAndFirestation
     * - Given existing address
     * - Then FamilyWithMedicalAndFirestationDTO
     */
    @Test
    public void givenExistingAddress_givenGetFamilyWithMedicalAndFirestation_thenReturnFamilyWithMedicalAndFirestationDTO() {

        // Given

        // Firestation at address
        Firestation firestation = new Firestation("1509 Culver St", 1);
        when(firestationRepository.getFirestationByAddress("1509 Culver St")).thenReturn(firestation);

        // Person covered by firestation
        Person person = new Person("John", "Boyd", "1509 Culver St", "Culver", "97451", "841-874-6512", "jboyd@mail.com");
        when(personRepository.getPersonByAddress("1509 Culver St")).thenReturn(List.of(person));

        // Medical record of person
        MedicalRecord medicalRecord = new MedicalRecord("John", "Boyd", "03/06/1984", new ArrayList<>(), new ArrayList<>());
        when(medicalRecordRepository.getMedicalRecordByFirstNameAndLastName("John", "Boyd")).thenReturn(medicalRecord);

        // Map to FamilyWithMedicalAndFirestationDTO
        FamilyWithMedicalAndFirestationDTO expectedDTO =
                new FamilyWithMedicalAndFirestationDTO(1, List.of(
                        new PersonMedicalDetailsDTO("John", "Boyd", 37, "841-874-6512", new ArrayList<>(), new ArrayList<>())
                ));
        when(emergencyMapper.toFamilyWithMedicalAndFirestationDTO(
                List.of(person),
                List.of(medicalRecord),
                firestation)).thenReturn(expectedDTO);

        // When
        FamilyWithMedicalAndFirestationDTO familyDTO = emergencyService.getFamilyWithMedicalAndFirestation("1509 Culver St");

        // Then
        verify(firestationRepository, times(1)).getFirestationByAddress("1509 Culver St");
        verify(personRepository, times(1)).getPersonByAddress("1509 Culver St");
        verify(medicalRecordRepository, times(1)).getMedicalRecordByFirstNameAndLastName("John", "Boyd");
        assertEquals(1, familyDTO.getStation());
        assertEquals(1, familyDTO.getPersonMedicalDetailDTOS().size());

    }

    /**
     * Testing method getFamilyWithMedicalAndFirestation
     * - Given non-existing address
     * - Then empty FamilyWithMedicalAndFirestationDTO
     */
    @Test
    public void givenNonExistingAddress_givenGetFamilyWithMedicalAndFirestation_thenReturnEmptyFamilyWithMedicalAndFirestationDTO() {
        // Given
        when(firestationRepository.getFirestationByAddress("UnknowAddress")).thenReturn(null);

        // When
        FamilyWithMedicalAndFirestationDTO familyDTO = emergencyService.getFamilyWithMedicalAndFirestation("UnknowAddress");

        // Then
        assertNull(familyDTO.getStation());
        assertNull(familyDTO.getPersonMedicalDetailDTOS());
    }

    /**
     * Testing method getFamilyWithMedicalGroupedByAddress
     * - Given existing station numbers
     * - Then FamilyWithMedicalGroupedByAddressDTO
     */
    @Test
    public void givenListOfExistingStationNumbers_whenGetFamilyWithMedicalGroupedByAddress_thenReturnFamilyWithMedicalGroupedByAddressDTO() {
        // Given
        List<Integer> stationNumbers = List.of(1);

        // Firestations
        List<Firestation> firestations = new ArrayList<>();
        firestations.add(new Firestation("1509 Culver St", 1));

        when(firestationRepository.getFirestationByStationNumber(1)).thenReturn(List.of(firestations.getFirst()));

        // Persons of firestation
        List<Person> persons = new ArrayList<>();
        persons.add(new Person("John", "Boyd", "1509 Culver St", "Culver", "97451", "841-874-6512", "jboyd@mail.com"));

        when(personRepository.getPersonByAddress("1509 Culver St")).thenReturn(List.of(persons.getFirst()));

        // Medical records of persons
        MedicalRecord medicalRecord = new MedicalRecord("John", "Boyd", "03/06/1984", new ArrayList<>(), new ArrayList<>());
        List<MedicalRecord> medicalRecords = List.of(medicalRecord);

        when(medicalRecordRepository.getMedicalRecordByFirstNameAndLastName("John", "Boyd")).thenReturn(medicalRecord);

        // Map persons and medical records
        List<PersonWithMedicalRecordDTO> personWithMedicalRecordDTOS = new ArrayList<>();
        personWithMedicalRecordDTOS.add(new PersonWithMedicalRecordDTO("John", "Boyd", "1509 Culver St", "Culver", "97451", "841-874-6512", "jboyd@mail.com", 37, true, new ArrayList<>(), new ArrayList<>()));

        when(emergencyMapper.toPersonWithMedicalRecord(persons, medicalRecords)).thenReturn(personWithMedicalRecordDTOS);

        // Map to PersonWithMedicalAndPhoneDTO
        PersonWithMedicalAndPhoneDTO personWithMedicalAndPhoneDTO =
                new PersonWithMedicalAndPhoneDTO("John", "Boyd", 37, "841-874-6512", new ArrayList<>(), new ArrayList<>());

        when(emergencyMapper.toPersonWithMedicalAndPhone(personWithMedicalRecordDTOS.getFirst())).thenReturn(personWithMedicalAndPhoneDTO);


        // When
        FamilyWithMedicalGroupedByAddressDTO familyDTO = emergencyService.getFamilyWithMedicalGroupedByAddress(stationNumbers);

        // Then
        verify(firestationRepository, times(1)).getFirestationByStationNumber(1);
        verify(personRepository, times(1)).getPersonByAddress("1509 Culver St");
        verify(medicalRecordRepository, times(1)).getMedicalRecordByFirstNameAndLastName("John", "Boyd");
        verify(emergencyMapper, times(1)).toPersonWithMedicalRecord(persons, medicalRecords);
        verify(emergencyMapper, times(1)).toPersonWithMedicalAndPhone(personWithMedicalRecordDTOS.getFirst());
        assertNotNull(familyDTO);
        assertEquals(1, familyDTO.getMapAddressPersons().size());
    }


    /**
     * Testing method getPersonMedicalWithEmail
     * - Given existing last name
     * - Then List of PersonWithMedicalAndEmailDTO
     */
    @Test
    public void givenExistingLastName_whenGetPersonMedicalWithEmail_thenReturnListOfPersonWithMedicalAndEmailDTO() {
        // Given

        // Persons with same last name
        List<Person> persons = new ArrayList<>();
        persons.add(new Person("John", "Boyd", "1509 Culver St", "Culver", "97451", "841-874-6512", "jboyd@mail.com"));

        when(personRepository.getPersonByLastName("Boyd")).thenReturn(persons);

        // Medical records of persons
        MedicalRecord medicalRecord = new MedicalRecord("John", "Boyd", "03/06/1984", new ArrayList<>(), new ArrayList<>());
        List<MedicalRecord> medicalRecords = List.of(medicalRecord);

        when(medicalRecordRepository.getMedicalRecordByFirstNameAndLastName("John", "Boyd")).thenReturn(medicalRecord);

        // Map persons and medical records
        List<PersonWithMedicalRecordDTO> personWithMedicalRecordDTOS = new ArrayList<>();
        personWithMedicalRecordDTOS.add(
                new PersonWithMedicalRecordDTO("John", "Boyd", "1509 Culver St", "Culver", "97451", "841-874-6512", "jboyd@mail.com", 37, true, new ArrayList<>(), new ArrayList<>())
        );

        when(emergencyMapper.toPersonWithMedicalRecord(persons, medicalRecords)).thenReturn(personWithMedicalRecordDTOS);

        // Map to PersonWithMedicalAndEmailDTO
        PersonWithMedicalAndEmailDTO personWithMedicalAndEmailDTO =
                new PersonWithMedicalAndEmailDTO("John", "Boyd", 37, "jboyd@mail.com", new ArrayList<>(), new ArrayList<>());

        when(emergencyMapper.toPersonWithMedicalAndEmailDTO(personWithMedicalRecordDTOS.getFirst())).thenReturn(personWithMedicalAndEmailDTO);

        // When
        List<PersonWithMedicalAndEmailDTO> personWithMedicalAndEmailDTOS = emergencyService.getPersonMedicalWithEmail("Boyd");

        // Then
        verify(personRepository, times(1)).getPersonByLastName("Boyd");
        verify(medicalRecordRepository, times(1)).getMedicalRecordByFirstNameAndLastName("John", "Boyd");
        verify(emergencyMapper, times(1)).toPersonWithMedicalRecord(persons, medicalRecords);
        verify(emergencyMapper, times(1)).toPersonWithMedicalAndEmailDTO(personWithMedicalRecordDTOS.getFirst());
        assertNotNull(personWithMedicalAndEmailDTOS);
        assertEquals(1, personWithMedicalAndEmailDTOS.size());
    }

    /**
     * Testing method getPersonMedicalWithEmail
     * - Given non-existing last name
     * - Then NotFoundException
     */
    @Test
    public void givenNonExistingLastName_whenGetPersonMedicalWithEmail_thenReturnNotFoundException() {
        // Given
        when(personRepository.getPersonByLastName("UnknowLastName")).thenReturn(new ArrayList<>());

        // When && Then
        assertThrows(NotFoundException.class, () -> emergencyService.getPersonMedicalWithEmail("UnknowLastName"));

    }


    /**
     * Testing method getPersonEmailByCity
     * - Given existing city
     * - Then List of email
     */
    @Test
    public void givenExistingCity_whenGetPersonEmailByCity_thenReturnListOfEmail() {
        // Given
        String city = "Culver";

        List<Person> persons = new ArrayList<>();
        persons.add(new Person("John", "Boyd", "1509 Culver St", city, "97451", "841-874-6512", "jboyd@mail.com"));

        when(personRepository.getPersons()).thenReturn(persons);

        // When
        HashSet<String> emails = emergencyService.getPersonEmailByCity(city);

        // Then
        verify(personRepository, times(1)).getPersons();
        assertEquals(1, emails.size());
    }

}
