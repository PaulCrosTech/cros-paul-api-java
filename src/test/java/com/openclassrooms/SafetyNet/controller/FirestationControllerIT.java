package com.openclassrooms.SafetyNet.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.SafetyNet.config.JsonTestConfig;
import com.openclassrooms.SafetyNet.model.Firestation;
import com.openclassrooms.SafetyNet.repository.JsonFileManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class FirestationControllerIT {

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
     * Test of getFirestations method
     * - Given a list of firestations
     * - Then OK and FirestationList
     *
     * @throws Exception exception
     */
    @Test
    public void givenFirestationList_whenGetFirestations_thenOkAndFirestationList() throws Exception {

        // Given

        // When
        ResultActions resultActions = mockMvc.perform(get("/firestations")
                .header("X-API-VERSION", 1));

        // Then
        resultActions.andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(12)));
    }

    /**
     * Test of deleteFirestationByAddress method
     * - Given an existing address
     * - Then OK and Firestation is deleted
     *
     * @throws Exception exception
     */
    @Test
    public void givenExistingAddress_whenDeleteFirestationByAddress_thenOkAndFirestationDeleted() throws Exception {
        // Given
        String address = "1509 Culver St";

        // When
        ResultActions resultActions = mockMvc.perform(delete("/firestation?address={address}", address)
                .header("X-API-VERSION", 1)
                .contentType(MediaType.APPLICATION_JSON));

        // Then
        resultActions.andExpect(status().isOk());
        assertTrue(
                jsonFileManager.getFirestations().stream()
                        .noneMatch(firestation -> firestation.getAddress().equals(address))
        );
    }


    /**
     * Test of deleteFirestationByAddress method
     * - Given a non-existing address
     * - Then Not Found and Firestation is not deleted
     *
     * @throws Exception exception
     */
    @Test
    public void givenNonExistingAddressn_whenDeleteFirestationByAddress_thenNotFoundAndFirestationNotDeleted() throws Exception {
        // Given
        String address = "unknow Address";


        // When
        ResultActions resultActions = mockMvc.perform(delete("/firestation?address={address}", address)
                .header("X-API-VERSION", 1)
                .contentType(MediaType.APPLICATION_JSON));

        // Then
        resultActions.andExpect(status().isNotFound());
    }

    /**
     * Test of saveFirestation method
     * - Given a new firestation
     * - Then Created and Firestation is created
     *
     * @throws Exception exception
     */
    @Test
    public void givenNewFirestation_whenSaveFirestation_thenCreatedAndFirestationIsCreated() throws Exception {
        // Given
        Firestation firestation = new Firestation("New Address", 999);
        ObjectMapper objectMapper = new ObjectMapper();
        String firestationToJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(firestation);


        // When
        ResultActions resultActions = mockMvc.perform(post("/firestation")
                .header("X-API-VERSION", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(firestationToJson));

        // Then
        resultActions.andExpect(status().isCreated());
        assertTrue(jsonFileManager.getFirestations().stream()
                .anyMatch(
                        firestation1 -> firestation1.getAddress().equals(firestation.getAddress())
                )
        );
    }

    /**
     * Test of saveFirestation method
     * - Given an existing firestation
     * - Then Conflict and Firestation is not created
     *
     * @throws Exception exception
     */
    @Test
    public void givenExistingFirestation_whenSaveFirestation_thenConflictAndFirestationIsNotCreated() throws Exception {
        // Given
        Firestation firestation = new Firestation("1509 Culver St", 999);
        ObjectMapper objectMapper = new ObjectMapper();
        String firestationToJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(firestation);


        // When
        ResultActions resultActions = mockMvc.perform(post("/firestation")
                .header("X-API-VERSION", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(firestationToJson));

        // Then
        resultActions.andExpect(status().isConflict());
        assertTrue(jsonFileManager
                .getFirestations()
                .stream()
                .noneMatch(f -> f.getAddress().equals(firestation.getAddress()) && f.getStation().equals(firestation.getStation()))
        );
    }

    /**
     * Test of saveFirestation method
     * - Given a malformed body
     * - Then Bad Request
     *
     * @throws Exception exception
     */
    @Test
    public void givenMalformedBody_whenSaveFirestation_thenBadRequest() throws Exception {
        // Given
        String body = "{\n" +
                "  \"address\": \"1509 Culver St\"\n" +
                "}";

        // When
        ResultActions resultActions = mockMvc.perform(post("/firestation")
                .header("X-API-VERSION", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body));

        // Then
        resultActions.andExpect(status().isBadRequest())
                .andDo(result -> System.out.println(result.getResponse().getContentAsString()));
    }


    /**
     * Test of updateFirestation method
     * - Given an existing firestation
     * - Then OK and Updated firestation
     *
     * @throws Exception exception
     */
    @Test
    public void givenExistingAddress_whenUpdateFirestation_thenOkAndUpdatedFirestation() throws Exception {
        // Given
        Firestation firestation = new Firestation("1509 Culver St", 999);
        ObjectMapper objectMapper = new ObjectMapper();
        String firestationToJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(firestation);

        // When
        ResultActions resultActions = mockMvc.perform(put("/firestation")
                .header("X-API-VERSION", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(firestationToJson));

        // Then
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.address").value(firestation.getAddress()))
                .andExpect(jsonPath("$.station").value(firestation.getStation()));
        assertTrue(jsonFileManager
                .getFirestations()
                .stream()
                .anyMatch(f -> f.getAddress().equals(firestation.getAddress()) && f.getStation().equals(firestation.getStation()))
        );
    }

    /**
     * Test of updateFirestation method
     * - Given an existing firestation
     * - Then Not Found
     *
     * @throws Exception exception
     */
    @Test
    public void givenNonExsitingAddress_whenUpdateFirestation_thenNotFound() throws Exception {
        // Given
        Firestation firestation = new Firestation("UnknowAddress", 999);
        ObjectMapper objectMapper = new ObjectMapper();
        String firestationToJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(firestation);

        // When
        ResultActions resultActions = mockMvc.perform(put("/firestation")
                .header("X-API-VERSION", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(firestationToJson));

        // Then
        resultActions.andExpect(status().isNotFound());
    }

    /**
     * Test of updateFirestation method
     * - Given a malformed body
     * - Then Bad Request
     *
     * @throws Exception exception
     */
    @Test
    public void givenMalformedBody_whenUpdateFirestation_thenBadRequest() throws Exception {
        // Given
        String body = "{\"address\": \"1509 Culver St\"}";

        // When
        ResultActions resultActions = mockMvc.perform(put("/firestation")
                .header("X-API-VERSION", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body));

        // Then
        resultActions.andExpect(status().isBadRequest())
                .andDo(result -> System.out.println(result.getResponse().getContentAsString()));
    }
}
