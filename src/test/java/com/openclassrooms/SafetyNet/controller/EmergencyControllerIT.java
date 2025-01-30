package com.openclassrooms.SafetyNet.controller;


import com.openclassrooms.SafetyNet.config.JsonTestConfig;
import com.openclassrooms.SafetyNet.utils.JsonFileManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;


import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class EmergencyControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JsonFileManager jsonFileManager;

    @BeforeEach
    public void setUpPerTest() throws Exception {
        // Load specific json file for tests
        JsonTestConfig.loadJsonTest(jsonFileManager);
    }

    /**
     * Test of getPersonCoveredByStation method
     * - Given an existing station number
     * - Then OK and PersonCoveredByStationDTO
     *
     * @throws Exception exception
     */
    @Test
    public void givenExistingStationNumber_whenGetPersonCoveredByStation_thenOkAndPersonCoveredByStationDTO() throws Exception {
        // Given
        int stationNumber = 3;

        // When
        ResultActions result = mockMvc.perform(get("/firestation?stationNumber=" + stationNumber)
                .header("X-API-VERSION", "1"));

        // Then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.persons", hasSize(11)))
                .andExpect(jsonPath("$.nbAdults").value(8))
                .andExpect(jsonPath("$.nbChildren").value(3));
    }

    /**
     * Test of getHouseChildren method
     * - Given an existing address
     * - Then OK and List of HouseChildrenDTO
     *
     * @throws Exception exception
     */
    @Test
    public void givenExistingAddress_whenGetHouseChildren_thenOkAndListOfHouseChildrenDTO() throws Exception {
        // Given
        String address = "1509 Culver St";

        // When
        ResultActions result = mockMvc.perform(get("/childAlert?address=" + address)
                .header("X-API-VERSION", "1"));

        // Then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].houseMembers", hasSize(4)))
                .andExpect(jsonPath("$[1].houseMembers", hasSize(4)));
    }

    /**
     * Test of getPhoneNumbersCoveredByFireStation method
     * - Given an existing station number
     * - Then OK and HashSet of phone numbers
     *
     * @throws Exception exception
     */
    @Test
    public void givenExistingStationNumber_whenGetPhoneNumbersCoveredByFireStation_thenOkAndPhoneNumbers() throws Exception {
        // Given
        int stationNumber = 3;

        // When
        ResultActions result = mockMvc.perform(get("/phoneAlert?firestation=" + stationNumber)
                .header("X-API-VERSION", "1"));

        // Then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(7)));
    }

    /**
     * Test of getFamilyWithMedicalAndFirestation method
     * - Given an existing address
     * - Then OK and FamilyWithMedicalAndFirestationDTO
     *
     * @throws Exception exception
     */
    @Test
    public void givenExistingAddress_whenGetFamilyWithMedicalAndFirestation_thenOkAndFamilyWithMedicalAndFirestationDTO() throws Exception {
        // Given
        String address = "1509 Culver St";

        // When
        ResultActions result = mockMvc.perform(get("/fire?address=" + address)
                .header("X-API-VERSION", "1"));

        // Then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.station").value(3))
                .andExpect(jsonPath("$.personMedicalDetail", hasSize(5)));
    }

    /**
     * Test of getFamilyWithMedicalGroupedByAddress method
     * - Given existing station numbers
     * - Then OK and FamilyWithMedicalGroupedByAddressDTO
     *
     * @throws Exception exception
     */
    @Test
    public void givenExistingStationNumbers_whenGetFamilyWithMedicalGroupedByAddress_thenOkAndFamilyWithMedicalGroupedByAddressDTO() throws Exception {

        // Given && When
        ResultActions result = mockMvc.perform(get("/flood/stations")
                .param("stations", "1", "3")
                .header("X-API-VERSION", "1"));

        // Then
        result.andExpect(status().isOk());
    }

    /**
     * Test of getPersonMedicalWithEmail method
     * - Given an existing lastname
     * - Then OK and PersonWithMedicalAndEmailDTO
     *
     * @throws Exception exception
     */
    @Test
    public void givenExistingLastname_whenGetPersonMedicalWithEmail_thenOkAndPersonWithMedicalAndEmailDTO() throws Exception {
        // Given
        String lastName = "Boyd";

        // When
        ResultActions result = mockMvc.perform(get("/personInfo?lastName=" + lastName)
                .header("X-API-VERSION", "1"));

        // Then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(6)));
    }

    /**
     * Test of getEmailsByCity method
     * - Given an existing city
     * - Then OK and HashSet of emails
     *
     * @throws Exception exception
     */
    @Test
    public void givenExistingCity_whenGetEmailsByCity_thenOkAndEmails() throws Exception {
        // Given
        String city = "Culver";

        // When
        ResultActions result = mockMvc.perform(get("/communityEmail?city=" + city)
                .header("X-API-VERSION", "1"));

        // Then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(15)));
    }
}