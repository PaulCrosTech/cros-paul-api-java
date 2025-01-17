package com.openclassrooms.SafetyNet;


import com.openclassrooms.SafetyNet.controller.EmergencyController;
import com.openclassrooms.SafetyNet.dto.*;
import com.openclassrooms.SafetyNet.exceptions.NotFoundException;
import com.openclassrooms.SafetyNet.model.Person;
import com.openclassrooms.SafetyNet.service.EmergencyService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.*;

import static org.hamcrest.Matchers.aMapWithSize;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tests for EmergencyController
 */
@WebMvcTest(controllers = EmergencyController.class)
public class EmergencyControllerTest {

    private final MockMvc mockMvc;
    private final String apiVersion = "1";

    private List<Person> persons;


    @MockitoBean
    private EmergencyService emergencyService;

    /**
     * Constructor
     *
     * @param mockMvc MockMvc
     */
    @Autowired
    public EmergencyControllerTest(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }

    /**
     * Testing GET /firestation with
     * - an existing station number
     * - Then OK and PersonCoveredByStationDTO
     *
     * @throws Exception Exception
     */
    @Test
    public void givenExistingStationNumber_whenGetPersonCoveredByStation_thenReturnPersonCoveredByStationDTO() throws Exception {
        // Given
        int stationNumber = 3;
        List<PersonBasicDetailsDTO> personsList = Arrays.asList(
                new PersonBasicDetailsDTO("John", "Boyd", "1509 Culver St", "841-874-6512"),
                new PersonBasicDetailsDTO("Jacob", "Boyd", "1509 Culver St", "841-874-6513"),
                new PersonBasicDetailsDTO("Tenley", "Boyd", "1509 Culver St", "841-874-6512")
        );
        PersonCoveredByStationDTO personCoveredByStationDTO = new PersonCoveredByStationDTO(1, 2, personsList);

        when(emergencyService.getPersonCoveredByStationNumber(stationNumber)).thenReturn(personCoveredByStationDTO);

        // When
        ResultActions result = mockMvc.perform(get("/firestation?stationNumber=" + stationNumber)
                .header("X-API-VERSION", apiVersion));

        // Then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.persons", hasSize(3)))
                .andExpect(jsonPath("$.nbAdults").value(2))
                .andExpect(jsonPath("$.nbChildren").value(1));
    }

    /**
     * Testing GET /firestation with
     * - an unknown station number
     * - Then NotFound
     *
     * @throws Exception Exception
     */
    @Test
    public void givenUnknowStationNumber_whenGetPersonCoveredByStation_thenReturnNotFound() throws Exception {
        // Given
        int stationNumber = 99;

        when(emergencyService.getPersonCoveredByStationNumber(stationNumber))
                .thenThrow(new NotFoundException("Station number not found"));

        // When
        ResultActions result = mockMvc.perform(get("/firestation?stationNumber=" + stationNumber)
                .header("X-API-VERSION", apiVersion));

        // Then
        result.andExpect(status().isNotFound());

    }


    /**
     * Testing GET /phoneAlert with
     * - an existing station number
     * - Then OK and FamilyDTO
     *
     * @throws Exception Exception
     */
    @Test
    public void givenExistingAddress_whenGetFamily_thenReturnFamilyDTO() throws Exception {
        // Given
        String address = "1509 Culver St";

        FamilyDTO familyDTO = new FamilyDTO(
                List.of(
                        new AdultDTO("John", "Boyd"),
                        new AdultDTO("Jacob", "Boyd")
                ),
                List.of(
                        new ChildrenDTO("Tenley", "Boyd", 10)
                )
        );

        when(emergencyService.getFamily(address)).thenReturn(familyDTO);

        // When
        ResultActions result = mockMvc.perform(get("/childAlert?address=" + address)
                .header("X-API-VERSION", apiVersion));

        // Then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.adults", hasSize(2)))
                .andExpect(jsonPath("$.children", hasSize(1)));
    }


    /**
     * Testing GET /phoneAlert with
     * - an existing station number
     * - Then OK and phone numbers
     *
     * @throws Exception Exception
     */
    @Test
    public void givenExistingStationNumber_whenGetPhoneNumbersCoveredByFireStation_thenReturnOkAndPhoneNumbers() throws Exception {
        // Given
        int stationNumber = 3;
        HashSet<String> phoneNumbers = new HashSet<>(Arrays.asList("841-874-6512", "841-874-6513"));

        when(emergencyService.getPhoneNumbersCoveredByFireStation(stationNumber)).thenReturn(phoneNumbers);

        // When
        ResultActions result = mockMvc.perform(get("/phoneAlert?firestation=" + stationNumber)
                .header("X-API-VERSION", apiVersion));

        // Then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    /**
     * Testing GET /phoneAlert with
     * - an unknown station number
     * - Then NotFound
     *
     * @throws Exception Exception
     */
    @Test
    public void givenUnknowStationNumber_whenGetPhoneNumbersCoveredByFireStation_thenNotFound() throws Exception {
        // Given
        int stationNumber = 99;

        when(emergencyService.getPhoneNumbersCoveredByFireStation(stationNumber))
                .thenThrow(new NotFoundException("Station number not found"));

        // When
        ResultActions result = mockMvc.perform(get("/phoneAlert?firestation=" + stationNumber)
                .header("X-API-VERSION", apiVersion));

        // Then
        result.andExpect(status().isNotFound());
    }


    /**
     * Testing GET /fire with
     * - an existing address
     * - Then OK and FamilyWithMedicalAndFirestationDTO
     *
     * @throws Exception Exception
     */
    @Test
    public void givenExistingAddress_whenGetFamilyWithMedicalAndFirestation_thenReturnOkAndFamilyWithMedicalAndFirestationDTO() throws Exception {
        // Given
        String address = "1509 Culver St";

        FamilyWithMedicalAndFirestationDTO familyDTO = new FamilyWithMedicalAndFirestationDTO(
                3,
                List.of(
                        new PersonMedicalDetailsDTO("John", "Boyd", List.of("aznol:350mg", "hydrapermazol:100mg"), List.of("nillacilan"), 36),
                        new PersonMedicalDetailsDTO("Jacob", "Boyd", List.of("pharmacol:5000mg", "terazine:10mg", "noznazol:250mg"), List.of(), 21)
                )
        );
        when(emergencyService.getFamilyWithMedicalAndFirestation(address)).thenReturn(familyDTO);

        // When
        ResultActions result = mockMvc.perform(get("/fire?address=" + address)
                .header("X-API-VERSION", apiVersion));

        // Then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.station").value(3))
                .andExpect(jsonPath("$.personMedicalDetailDTOS", hasSize(2)));

    }

    /**
     * Testing GET /flood/stations with
     * - an existing station number
     * - Then OK and FamilyWithMedicalGroupedByAddressDTO
     *
     * @throws Exception Exception
     */
    @Test
    public void givenExistingStationNumbers_whenGetFamilyWithMedicalGroupedByAddress_thenReturneOkAndFamilyWithMedicalGroupedByAddressDTO() throws Exception {
        // Given
        HashMap<String, List<PersonWithMedicalAndPhoneDTO>> addressList = new HashMap<>();
        addressList.put("1509 Culver St", List.of(
                new PersonWithMedicalAndPhoneDTO("John", "Boyd", List.of("aznol:350mg", "hydrapermazol:100mg"), List.of("nillacilan"), 36, "841-874-6512"),
                new PersonWithMedicalAndPhoneDTO("Jacob", "Boyd", List.of("pharmacol:5000mg", "terazine:10mg", "noznazol:250mg"), List.of(), 31, "841-874-6513")
        ));
        addressList.put("834 Binoc Ave", List.of(
                new PersonWithMedicalAndPhoneDTO("Jonanathan", "Marrack", List.of("aznol:350mg"), List.of(), 50, "841-874-6512")
        ));

        FamilyWithMedicalGroupedByAddressDTO familyDTO = new FamilyWithMedicalGroupedByAddressDTO(addressList);

        when(emergencyService.getFamilyWithMedicalGroupedByAddress(List.of(2, 3)))
                .thenReturn(familyDTO);
        // When
        ResultActions result = mockMvc.perform(get("/flood/stations")
                .param("stations", "2", "3")
                .header("X-API-VERSION", apiVersion));

        // Then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.address", aMapWithSize(2)))
                .andExpect(jsonPath("$.address.['1509 Culver St']", hasSize(2)))
                .andExpect(jsonPath("$.address.['834 Binoc Ave']", hasSize(1)));
    }


    /**
     * Testing GET /personInfo with
     * - an existing lastname
     * - Then OK and PersonWithMedicalAndEmailDTO
     *
     * @throws Exception Exception
     */
    @Test
    public void givenExistingLastname_whenGetPersonMedicalWithEmail_thenReturnOkAndPersonWithMedicalAndEmailDTO() throws Exception {
        // Given
        String lastName = "Boyd";
        List<PersonWithMedicalAndEmailDTO> personDTO = List.of(
                new PersonWithMedicalAndEmailDTO("John", "Boyd", List.of("aznol:350mg", "hydrapermazol:100mg"), List.of("nillacilan"), 36, "john@mail.com"),
                new PersonWithMedicalAndEmailDTO("Jacob", "Boyd", List.of("pharmacol:5000mg", "terazine:10mg", "noznazol:250mg"), List.of(), 31, "jacob@mail.com")
        );

        when(emergencyService.getPersonMedicalWithEmail(lastName)).thenReturn(personDTO);

        // When
        ResultActions result = mockMvc.perform(get("/personInfo?lastName=" + lastName)
                .header("X-API-VERSION", apiVersion));

        // Then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    /**
     * Testing GET /personInfo with
     * - an unknown lastname
     * - Then NotFound
     *
     * @throws Exception Exception
     */
    @Test
    public void givenUnknowLastname_whenGetPersonMedicalWithEmail_thenReturnNotFound() throws Exception {
        // Given
        String lastName = "UnknowLastName";

        when(emergencyService.getPersonMedicalWithEmail(lastName)).thenThrow(
                new NotFoundException("No person found with last name " + lastName)
        );

        // When
        ResultActions result = mockMvc.perform(get("/personInfo?lastName=" + lastName)
                .header("X-API-VERSION", apiVersion));

        // Then
        result.andExpect(status().isNotFound());
    }

    /**
     * Testing GET /communityEmail with
     * - an existing city
     * - Then OK and emails
     *
     * @throws Exception Exception
     */
    @Test
    public void givenExistingCity_whenGetEmailsByCity_thenReturnOkAndEmails() throws Exception {
        // Given
        String city = "Culver";
        HashSet<String> emails = new HashSet<>(Arrays.asList("mail1@mail.com", "mail2@mail.com"));

        when(emergencyService.getPersonEmailByCity(city)).thenReturn(emails);

        // When
        ResultActions result = mockMvc.perform(get("/communityEmail?city=" + city)
                .header("X-API-VERSION", apiVersion));

        // Then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }
}
