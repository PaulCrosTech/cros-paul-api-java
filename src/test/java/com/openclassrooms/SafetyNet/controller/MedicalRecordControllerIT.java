package com.openclassrooms.SafetyNet.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.SafetyNet.config.JsonTestConfig;
import com.openclassrooms.SafetyNet.model.MedicalRecord;
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

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class MedicalRecordControllerIT {

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
     * Test of getMedicalRecords method
     * - Given a list of medical records
     * - Then OK and MedicalRecordList
     *
     * @throws Exception exception
     */
    @Test
    public void givenMedicalRecordList_whenGetMedicalRecords_thenOkAndMedicalRecordList() throws Exception {

        // Given

        // When
        ResultActions resultActions = mockMvc.perform(get("/medicalRecords")
                .header("X-API-VERSION", 1));

        // Then
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(23)));
    }

    /**
     * Test of DeleteMedicalRecordByFirstNameAndLastName method
     * - Given an existing medical record
     * - Then OK and MedicalRecord is deleted
     *
     * @throws Exception exception
     */
    @Test
    public void givenExistingName_whenDeleteMedicalRecordByFirstNameAndLastName_thenOkAndMedicalRecordIsDeleted() throws Exception {
        // Given
        String firstName = "John";
        String lastName = "Boyd";

        // When
        ResultActions resultActions = mockMvc.perform(delete("/medicalRecord?firstName={firstName}&lastName={lastName}", firstName, lastName)
                .header("X-API-VERSION", 1)
                .contentType(MediaType.APPLICATION_JSON));

        // Then
        resultActions.andExpect(status().isOk());
        assertTrue(
                jsonFileManager.getMedicalRecords()
                        .stream()
                        .noneMatch(mr -> mr.getFirstName().equals(firstName) && mr.getLastName().equals(lastName))
        );
    }

    /**
     * Test of DeleteMedicalRecordByFirstNameAndLastName method
     * - Given a non-existing medical record
     * - Then Not Found
     *
     * @throws Exception exception
     */
    @Test
    public void givenNonExistingName_whenDeleteMedicalRecordByFirstNameAndLastName_thenNotFound() throws Exception {
        // Given
        String firstName = "UnknownFirstName";
        String lastName = "UnknownLastName";

        // When
        ResultActions resultActions = mockMvc.perform(delete("/medicalRecord?firstName={firstName}&lastName={lastName}", firstName, lastName)
                .header("X-API-VERSION", 1)
                .contentType(MediaType.APPLICATION_JSON));

        // Then
        resultActions.andExpect(status().isNotFound());
    }

    /**
     * Test of CreateMedicalRecord method
     * - Given a new medical record for an existing person
     * - Then Created and MedicalRecord is created
     *
     * @throws Exception exception
     */
    @Test
    public void givenNewMedicalRecordForExistingPerson_whenCreateMedicalRecord_thenCreatedAndMedicalRecordIsCreated() throws Exception {
        // Given
        MedicalRecord mrExpected = new MedicalRecord("John", "Doe", "01/01/1980", Arrays.asList("aznol:350mg", "hydrapermazol:100mg"), List.of("nillacilan"));
        ObjectMapper objectMapper = new ObjectMapper();
        String medicalRecordToJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(mrExpected);

        // When
        ResultActions resultActions = mockMvc.perform(post("/medicalRecord")
                .header("X-API-VERSION", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(medicalRecordToJson));

        // Then
        resultActions.andExpect(status().isCreated());
        MedicalRecord mrActual = jsonFileManager.getMedicalRecords()
                .stream()
                .filter(
                        md -> md.getFirstName().equals(mrExpected.getFirstName()) && md.getLastName().equals(mrExpected.getLastName())
                )
                .findFirst()
                .orElse(null);

        assertEquals(mrExpected, mrActual);
    }

    /**
     * Test of CreateMedicalRecord method
     * - Given a new medical record for a non-existing person
     * - Then Not Found for the person
     *
     * @throws Exception exception
     */
    @Test
    public void givenNewMedicalRecordForNonExistingPerson_whenCreateMedicalRecord_thenNotFound() throws Exception {
        // Given
        MedicalRecord mrExpected = new MedicalRecord("UnknowFirstName", "UnknowLastName", "01/01/1980", Arrays.asList("aznol:350mg", "hydrapermazol:100mg"), List.of("nillacilan"));
        ObjectMapper objectMapper = new ObjectMapper();
        String medicalRecordToJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(mrExpected);

        // When
        ResultActions resultActions = mockMvc.perform(post("/medicalRecord")
                .header("X-API-VERSION", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(medicalRecordToJson));

        // Then
        resultActions.andExpect(status().isNotFound())
                .andDo(result -> System.out.println(result.getResponse().getContentAsString()));
    }

    /**
     * Test of CreateMedicalRecord method
     * - Given an existing medical record
     * - Then Conflict
     *
     * @throws Exception exception
     */
    @Test
    public void givenExistingMedicalRecord_whenCreateMedicalRecord_thenConflict() throws Exception {
        // Given
        MedicalRecord medicalRecord = jsonFileManager.getMedicalRecords().getFirst();
        ObjectMapper objectMapper = new ObjectMapper();
        String medicalRecordToJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(medicalRecord);


        // When
        ResultActions resultActions = mockMvc.perform(post("/medicalRecord")
                .header("X-API-VERSION", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(medicalRecordToJson));

        // Then
        resultActions.andExpect(status().isConflict());
    }


    /**
     * Test of CreateMedicalRecord method
     * - Given a malformed body
     * - Then Bad Request
     *
     * @throws Exception exception
     */
    @Test
    public void givenMalformedBody_whenCreateMedicalRecord_thenBadRequest() throws Exception {
        // Given
        String body = "{\n" +
                "  \"firstName\": \"John\",\n" +
                "  \"lastName\": \"Boyd\",\n" +
                "  \"medications\": [\n" +
                "    \"aznol:350mg\",\n" +
                "    \"hydrapermazol:100mg\"\n" +
                "  ]\n" +
                "}";
        // When
        ResultActions resultActions = mockMvc.perform(post("/medicalRecord")
                .header("X-API-VERSION", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body));

        // Then
        resultActions.andExpect(status().isBadRequest())
                .andDo(result -> System.out.println(result.getResponse().getContentAsString()));
    }


    /**
     * Test of UpdateMedicalRecord method
     * - Given an existing medical record for an existing person
     * - Then OK and MedicalRecord is updated
     *
     * @throws Exception exception
     */
    @Test
    public void givenExistingMedicalRecordForExistingPerson_whenUpdateMedicalRecord_thenOkAndMedicalRecordIsUpdated() throws Exception {
        // Given
        MedicalRecord mrExpected = new MedicalRecord("John", "Boyd", "01/01/1955", Arrays.asList("aaaa:666mg", "bbbb:555mg"), List.of("ccc", "ddd"));
        ObjectMapper objectMapper = new ObjectMapper();
        String medicalRecordToJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(mrExpected);


        // When
        ResultActions resultActions = mockMvc.perform(put("/medicalRecord")
                .header("X-API-VERSION", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(medicalRecordToJson));

        // Then
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value(mrExpected.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(mrExpected.getLastName()))
                .andExpect(jsonPath("$.birthdate").value(mrExpected.getBirthdate()))
                .andExpect(jsonPath("$.medications").isArray())
                .andExpect(jsonPath("$.medications", hasSize(2)))
                .andExpect(jsonPath("$.medications[0]").value(mrExpected.getMedications().get(0)))
                .andExpect(jsonPath("$.medications[1]").value(mrExpected.getMedications().get(1)))
                .andExpect(jsonPath("$.allergies").isArray())
                .andExpect(jsonPath("$.allergies", hasSize(2)))
                .andExpect(jsonPath("$.allergies[0]").value(mrExpected.getAllergies().get(0)))
                .andExpect(jsonPath("$.allergies[1]").value(mrExpected.getAllergies().get(1)));
//                .andDo(result -> System.out.println(result.getResponse().getContentAsString()));

        MedicalRecord mrActual = jsonFileManager.getMedicalRecords()
                .stream()
                .filter(mr -> mr.getFirstName().equals(mrExpected.getFirstName()) && mr.getLastName().equals(mrExpected.getLastName()))
                .findFirst()
                .orElse(null);
        assertEquals(mrExpected, mrActual);
    }

    /**
     * Test of UpdateMedicalRecord method
     * - Given an existing medical record for a non-existing person
     * - Then Not Found for the person
     *
     * @throws Exception exception
     */
    @Test
    public void givenExistingMedicalRecordForNonExsitingPerson_whenUpdateMedicalRecord_thenNotFound() throws Exception {
        // Given
        MedicalRecord mrExpected = new MedicalRecord("UnknowFirstName", "UnknowLastName", "01/01/1980", Arrays.asList("aznol:350mg", "hydrapermazol:100mg"), List.of("nillacilan"));
        ObjectMapper objectMapper = new ObjectMapper();
        String medicalRecordToJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(mrExpected);

        // When
        ResultActions resultActions = mockMvc.perform(put("/medicalRecord")
                .header("X-API-VERSION", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(medicalRecordToJson));

        // Then
        resultActions.andExpect(status().isNotFound());

    }

    /**
     * Test of UpdateMedicalRecord method
     * - Given a non-existing medical record for an existing person
     * - Then Not Found for medical record
     *
     * @throws Exception exception
     */
    @Test
    public void givenNonExistingMedicalRecordForExistingPerson_whenUpdateMedicalRecord_thenNotFound() throws Exception {
        // Given
        MedicalRecord mrExpected = new MedicalRecord("John", "Doe", "01/01/1980", Arrays.asList("aznol:350mg", "hydrapermazol:100mg"), List.of("nillacilan"));
        ObjectMapper objectMapper = new ObjectMapper();
        String medicalRecordToJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(mrExpected);

        // When
        ResultActions resultActions = mockMvc.perform(put("/medicalRecord")
                .header("X-API-VERSION", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(medicalRecordToJson));

        // Then
        resultActions.andExpect(status().isNotFound());

    }

    /**
     * Test of UpdateMedicalRecord method
     * - Given a malformed body
     * - Then Bad Request
     *
     * @throws Exception exception
     */
    @Test
    public void givenMalformedBody_whenUpdateMedicalRecord_thenBadRequest() throws Exception {
        // Given
        String body = "{\n" +
                "  \"firstName\": \"John\",\n" +
                "  \"lastName\": \"Boyd\",\n" +
                "  \"allergies\": [\n" +
                "    \"nillacilan\"\n" +
                "  ]\n" +
                "}";

        // When
        ResultActions resultActions = mockMvc.perform(put("/medicalRecord")
                .header("X-API-VERSION", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body));

        // Then
        resultActions.andExpect(status().isBadRequest())
                .andDo(result -> System.out.println(result.getResponse().getContentAsString()));
    }

}
