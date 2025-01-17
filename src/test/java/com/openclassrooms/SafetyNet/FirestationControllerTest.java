package com.openclassrooms.SafetyNet;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.SafetyNet.controller.FirestationController;
import com.openclassrooms.SafetyNet.exceptions.ConflictException;
import com.openclassrooms.SafetyNet.exceptions.NotFoundException;
import com.openclassrooms.SafetyNet.model.Firestation;
import com.openclassrooms.SafetyNet.service.FirestationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * FirestationControllerTest
 */
@WebMvcTest(controllers = FirestationController.class)
public class FirestationControllerTest {

    private final MockMvc mockMvc;
    private List<Firestation> firestations;
    private final String apiVersion = "1";

    @MockitoBean
    private FirestationService firestationService;

    /**
     * Constructor
     *
     * @param mockMvc MockMvc
     */
    @Autowired
    public FirestationControllerTest(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }

    /**
     * Setup before each test
     */
    @BeforeEach
    public void setUpPerTest() {
        firestations = Arrays.asList(
                new Firestation("1509 Culver St", 3),
                new Firestation("29 15th St", 2),
                new Firestation("834 Binoc Ave", 3)
        );
    }

    /**
     * Testing route GET /firestations
     * - Given  firestation list
     * - Then OK and firestation list
     *
     * @throws Exception Exception
     */
    @Test
    public void givenFirestationList_whenGetFirestations_thenReturnFirestationList() throws Exception {

        // Given
        when(firestationService.getFirestations()).thenReturn(firestations);

        // When
        ResultActions resultActions = mockMvc.perform(get("/firestations")
                .header("X-API-VERSION", apiVersion)
                .contentType(MediaType.APPLICATION_JSON));

        // Then
        resultActions.andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(3)));
    }


    /**
     * Testing route DELETE /firestation with address as query parameter
     * - Given an existing address
     * - Then OK
     *
     * @throws Exception Exception
     */
    @Test
    public void givenExistingAddress_whenDeleteFirestationByAddress_thenReturnOk() throws Exception {
        // Given
        String address = "1509 Culver St";

        doNothing().when(firestationService).deleteFirestationByAddress(address);

        // When
        ResultActions resultActions = mockMvc.perform(delete("/firestation?address={address}", address)
                .header("X-API-VERSION", apiVersion)
                .contentType(MediaType.APPLICATION_JSON));

        // Then
        resultActions.andExpect(status().isOk());
    }

    /**
     * Testing route DELETE /firestation with address as query parameter
     * - Given a non-existing address
     * - Then NotFound
     *
     * @throws Exception Exception
     */
    @Test
    public void givenNonExistingAddressn_whenDeleteFirestationByAddress_thenReturnNotFound() throws Exception {
        // Given
        String address = "unknow Address";

        doThrow(new NotFoundException("Fire station not found with address : " + address))
                .when(firestationService).deleteFirestationByAddress(address);

        // When
        ResultActions resultActions = mockMvc.perform(delete("/firestation?address={address}", address)
                .header("X-API-VERSION", apiVersion)
                .contentType(MediaType.APPLICATION_JSON));

        // Then
        resultActions.andExpect(status().isNotFound());
    }

    /**
     * Testing route POST /firestation
     * - Given a new firestation
     * - Then Created
     *
     * @throws Exception Exception
     */
    @Test
    public void givenNewFirestation_whenSaveFirestation_thenReturnCreated() throws Exception {
        // Given
        Firestation firestation = new Firestation("New Address", 1);
        ObjectMapper objectMapper = new ObjectMapper();
        String firestationToJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(firestation);

        doNothing().when(firestationService).saveFirestation(firestation);

        // When
        ResultActions resultActions = mockMvc.perform(post("/firestation")
                .header("X-API-VERSION", apiVersion)
                .contentType(MediaType.APPLICATION_JSON)
                .content(firestationToJson));

        // Then
        resultActions.andExpect(status().isCreated());
    }

    /**
     * Testing route POST /firestation
     * - Given an existing firestation
     * - Then Conflict
     *
     * @throws Exception Exception
     */
    @Test
    public void givenExistingFirestation_whenSaveFirestation_thenReturnConflict() throws Exception {
        // Given
        Firestation firestation = new Firestation("1509 Culver St", 1);
        ObjectMapper objectMapper = new ObjectMapper();
        String firestationToJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(firestation);

        doThrow(new ConflictException("Fire station already exist with address: " + firestation.getAddress()))
                .when(firestationService).saveFirestation(firestation);

        // When
        ResultActions resultActions = mockMvc.perform(post("/firestation")
                .header("X-API-VERSION", apiVersion)
                .contentType(MediaType.APPLICATION_JSON)
                .content(firestationToJson));

        // Then
        resultActions.andExpect(status().isConflict());
    }


    /**
     * Testing route POST /firestation
     * - Given a malformed body
     * - Then BadRequest
     *
     * @throws Exception Exception
     */
    @Test
    public void givenMalformedBody_whenSaveFirestation_thenReturnBadRequest() throws Exception {
        // Given
        String body = "{\n" +
                "  \"address\": \"1509 Culver St\"\n" +
                "}";

        // When
        ResultActions resultActions = mockMvc.perform(post("/firestation")
                .header("X-API-VERSION", apiVersion)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body));

        // Then
        resultActions.andExpect(status().isBadRequest())
                .andDo(result -> System.out.println(result.getResponse().getContentAsString()));
    }

    /**
     * Testing route PUT /firestation
     * - Given a non-existing firestation
     * - Then NotFound
     *
     * @throws Exception Exception
     */
    @Test
    public void givenNonExsitingAddress_whenUpdateFirestation_thenReturnNotFound() throws Exception {
        // Given
        Firestation firestation = new Firestation("UnknowAddress", 9);
        ObjectMapper objectMapper = new ObjectMapper();
        String firestationToJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(firestation);

        when(firestationService.updateFirestation(firestation))
                .thenThrow(new NotFoundException("Fire station not found with address: " + firestation.getAddress()));

        // When
        ResultActions resultActions = mockMvc.perform(put("/firestation")
                .header("X-API-VERSION", apiVersion)
                .contentType(MediaType.APPLICATION_JSON)
                .content(firestationToJson));

        // Then
        resultActions.andExpect(status().isNotFound());


    }

    /**
     * Testing route PUT /firestation
     * - Given an existing firestation
     * - Then Ok and updated firestation
     *
     * @throws Exception Exception
     */
    @Test
    public void givenExistingAddress_whenUpdateFirestation_thenReturnOkAndUpdatedFirestation() throws Exception {
        // Given
        Firestation firestation = new Firestation("1509 Culver St", 99);
        ObjectMapper objectMapper = new ObjectMapper();
        String firestationToJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(firestation);

        when(firestationService.updateFirestation(firestation)).thenReturn(firestation);

        // When
        ResultActions resultActions = mockMvc.perform(put("/firestation")
                .header("X-API-VERSION", apiVersion)
                .contentType(MediaType.APPLICATION_JSON)
                .content(firestationToJson));

        // Then
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.address").value("1509 Culver St"))
                .andExpect(jsonPath("$.station").value("99"));
    }

    /**
     * Testing route PUT /firestation
     * - Given a malformed body
     * - Then BadRequest
     *
     * @throws Exception Exception
     */
    @Test
    public void givenMalformedBody_whenUpdateFirestation_thenReturnBadRequest() throws Exception {
        // Given
        String body = "{\"address\": \"1509 Culver St\"}";
        // When
        ResultActions resultActions = mockMvc.perform(put("/firestation")
                .header("X-API-VERSION", apiVersion)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body));

        // Then
        resultActions.andExpect(status().isBadRequest())
                .andDo(result -> System.out.println(result.getResponse().getContentAsString()));
    }

}
