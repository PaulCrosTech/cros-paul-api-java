package com.openclassrooms.SafetyNet;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.SafetyNet.controller.MedicalRecordController;
import com.openclassrooms.SafetyNet.exceptions.ConflictException;
import com.openclassrooms.SafetyNet.exceptions.NotFoundException;
import com.openclassrooms.SafetyNet.model.MedicalRecord;
import com.openclassrooms.SafetyNet.service.MedicalRecordService;
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

@WebMvcTest(controllers = MedicalRecordController.class)
public class MedicalRecordControllerTest {

    private final MockMvc mockMvc;
    private List<MedicalRecord> medicalRecords;
    private final String apiVersion = "1";

    @MockitoBean
    private MedicalRecordService medicalRecordService;

    @Autowired
    public MedicalRecordControllerTest(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }

    @BeforeEach
    public void setUp() {
        medicalRecords = Arrays.asList(
                new MedicalRecord("John", "Boyd", "03/06/1984", Arrays.asList("aznol:350mg", "hydrapermazol:100mg"), List.of("nillacilan")),
                new MedicalRecord("Jacob", "Boyd", "03/06/1989", Arrays.asList("pharmacol:5000mg", "terazine:10mg", "noznazol:250mg"), List.of()),
                new MedicalRecord("Tenley", "Boyd", "02/18/2012", List.of(), List.of("peanut"))
        );
    }

    /**
     * Testing route GET /medicalRecords
     *
     * @throws Exception Exception
     */
    @Test
    public void givenMedicalRecordList_whenGetMedicalRecords_thenReturnMedicalRecordList() throws Exception {

        // Given
        when(medicalRecordService.getMedicalRecords()).thenReturn(medicalRecords);

        // When
        ResultActions resultActions = mockMvc.perform(get("/medicalRecords")
                .header("X-API-VERSION", apiVersion)
                .contentType(MediaType.APPLICATION_JSON));

        // Then
        resultActions.andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(3)));
    }


    /**
     * Testing route GET /medicalRecord?firstName={firstName}&lastName={lastName} with an existing person
     *
     * @throws Exception Exception
     */
    @Test
    public void givenExistingPerson_whenGetMedicalRecordByFirstNameAndLastName_thenReturnOkAndMedicalRecord() throws Exception {
        // Given
        String firstName = "John";
        String lastName = "Boyd";

        when(medicalRecordService.getMedicalRecordByFirstNameAndLastName(firstName, lastName)).thenReturn(medicalRecords.getFirst());

        // When
        ResultActions resultActions = mockMvc.perform(get("/medicalRecord?firstName={firstName}&lastName={lastName}", firstName, lastName)
                .header("X-API-VERSION", apiVersion)
                .contentType(MediaType.APPLICATION_JSON));

        // Then
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Boyd"));
    }


    /**
     * Testing route GET /medicalRecord?firstName={firstName}&lastName={lastName} with a non-existing person
     *
     * @throws Exception Exception
     */
    @Test
    public void givenNonExistingPerson_whenGetMedicalRecordByFirstNameAndLastName_thenReturnNotFound() throws Exception {
        // Given
        String firstName = "UnknownFirstName";
        String lastName = "UnknownLastName";

        when(medicalRecordService.getMedicalRecordByFirstNameAndLastName(firstName, lastName)).
                thenThrow(new NotFoundException("Medical record not found with firstName: " + firstName + " and lastName: " + lastName));

        // When
        ResultActions resultActions = mockMvc.perform(get("/medicalRecord?firstName={firstName}&lastName={lastName}", firstName, lastName)
                .header("X-API-VERSION", apiVersion)
                .contentType(MediaType.APPLICATION_JSON));

        // Then
        resultActions.andExpect(status().isNotFound());

    }


    /**
     * Testing route DELETE /medicalRecord?firstName={firstName}&lastName={lastName} with an existing person
     *
     * @throws Exception Exception
     */
    @Test
    public void givenExistingPerson_whenDeleteMedicalRecord_thenReturnOk() throws Exception {
        // Given
        String firstName = "John";
        String lastName = "Boyd";

        doNothing().when(medicalRecordService).deleteMedicalRecordByFirstNameAndLastName(firstName, lastName);

        // When
        ResultActions resultActions = mockMvc.perform(delete("/medicalRecord?firstName={firstName}&lastName={lastName}", firstName, lastName)
                .header("X-API-VERSION", apiVersion)
                .contentType(MediaType.APPLICATION_JSON));

        // Then
        resultActions.andExpect(status().isOk());
    }

    /**
     * Testing route DELETE /medicalRecord?firstName={firstName}&lastName={lastName} with a non-existing person
     *
     * @throws Exception Exception
     */
    @Test
    public void givenNonExistingPerson_whenDeleteMedicalRecord_thenReturnNotFound() throws Exception {
        // Given
        String firstName = "UnknownFirstName";
        String lastName = "UnknownLastName";

        doThrow(new NotFoundException("Medical record not found with firstName: " + firstName + " and lastName: " + lastName))
                .when(medicalRecordService).deleteMedicalRecordByFirstNameAndLastName(firstName, lastName);

        // When
        ResultActions resultActions = mockMvc.perform(delete("/medicalRecord?firstName={firstName}&lastName={lastName}", firstName, lastName)
                .header("X-API-VERSION", apiVersion)
                .contentType(MediaType.APPLICATION_JSON));

        // Then
        resultActions.andExpect(status().isNotFound());
    }


    /**
     * Testing route POST /medicalRecord with a new medical record
     *
     * @throws Exception Exception
     */
    @Test
    public void givenNewPerson_whenCreateMedicalRecord_thenReturnCreated() throws Exception {
        // Given
        MedicalRecord medicalRecord = new MedicalRecord("NewFirstName", "NewLastName", "01/01/1980", Arrays.asList("aznol:350mg", "hydrapermazol:100mg"), List.of("nillacilan"));

        ObjectMapper objectMapper = new ObjectMapper();
        String medicalRecordToJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(medicalRecord);

        doNothing().when(medicalRecordService).saveMedicalRecord(medicalRecord);

        // When
        ResultActions resultActions = mockMvc.perform(post("/medicalRecord")
                .header("X-API-VERSION", apiVersion)
                .contentType(MediaType.APPLICATION_JSON)
                .content(medicalRecordToJson));

        // Then
        resultActions.andExpect(status().isCreated());
    }

    /**
     * Testing route POST /medicalRecord with an existing medical record
     *
     * @throws Exception Exception
     */
    @Test
    public void givenExistingPerson_whenCreateMedicalRecord_thenReturnConflict() throws Exception {
        // Given
        MedicalRecord medicalRecord = medicalRecords.getFirst();
        ObjectMapper objectMapper = new ObjectMapper();
        String medicalRecordToJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(medicalRecords.getFirst());

        doThrow(new ConflictException("Medical record already exist with firstName: " + medicalRecord.getFirstName() + " and lastName: " + medicalRecord.getLastName()))
                .when(medicalRecordService).saveMedicalRecord(medicalRecord);

        // When
        ResultActions resultActions = mockMvc.perform(post("/medicalRecord")
                .header("X-API-VERSION", apiVersion)
                .contentType(MediaType.APPLICATION_JSON)
                .content(medicalRecordToJson));

        // Then
        resultActions.andExpect(status().isConflict());
    }


    /**
     * Testing route POST /medicalRecord with a malformed body
     *
     * @throws Exception Exception
     */
    @Test
    public void givenMalformedBody_whenCreateMedicalRecord_thenReturnBadRequest() throws Exception {
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
                .header("X-API-VERSION", apiVersion)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body));

        // Then
        resultActions.andExpect(status().isBadRequest())
                .andDo(result -> System.out.println(result.getResponse().getContentAsString()));
    }


    /**
     * Testing route PUT /medicalRecord with a non-existing person
     *
     * @throws Exception Exception
     */
    @Test
    public void givenNonExsitingPerson_whenUpdateMedicalRecord_thenReturnNotFound() throws Exception {
        // Given
        MedicalRecord medicalRecord = new MedicalRecord("NewFirstName", "NewLastName", "03/06/1984", Arrays.asList("aznol:350mg", "hydrapermazol:100mg"), List.of("nillacilan"));
        ObjectMapper objectMapper = new ObjectMapper();
        String medicalRecordToJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(medicalRecord);

        when(medicalRecordService.updateMedicalRecord(medicalRecord))
                .thenThrow(new NotFoundException("Medical record not found with firstName: " + medicalRecord.getFirstName()
                        + " and lastName: " + medicalRecord.getLastName()));

        // When
        ResultActions resultActions = mockMvc.perform(put("/medicalRecord")
                .header("X-API-VERSION", apiVersion)
                .contentType(MediaType.APPLICATION_JSON)
                .content(medicalRecordToJson));

        // Then
        resultActions.andExpect(status().isNotFound());


    }

    /**
     * Testing route PUT /medicalRecord with an existing person
     *
     * @throws Exception Exception
     */
    @Test
    public void givenExistingPerson_whenUpdateMedicalRecord_thenReturnOkAndUpdatedMedicalRecord() throws Exception {
        // Given
        MedicalRecord medicalRecord = new MedicalRecord("NewFirstName", "NewLastName", "03/06/1984", Arrays.asList("aznol:350mg", "hydrapermazol:100mg"), List.of("nillacilan"));
        ObjectMapper objectMapper = new ObjectMapper();
        String medicalRecordToJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(medicalRecord);

        when(medicalRecordService.updateMedicalRecord(medicalRecord)).thenReturn(medicalRecord);

        // When
        ResultActions resultActions = mockMvc.perform(put("/medicalRecord")
                .header("X-API-VERSION", apiVersion)
                .contentType(MediaType.APPLICATION_JSON)
                .content(medicalRecordToJson));

        // Then
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("NewFirstName"))
                .andExpect(jsonPath("$.lastName").value("NewLastName"))
                .andExpect(jsonPath("$.birthdate").value("03/06/1984"))
                .andExpect(jsonPath("$.medications").isArray())
                .andExpect(jsonPath("$.medications", hasSize(2)))
                .andExpect(jsonPath("$.medications[0]").value("aznol:350mg"))
                .andExpect(jsonPath("$.medications[1]").value("hydrapermazol:100mg"))
                .andExpect(jsonPath("$.allergies").isArray())
                .andExpect(jsonPath("$.allergies", hasSize(1)))
                .andExpect(jsonPath("$.allergies[0]").value("nillacilan"))
                .andDo(result -> System.out.println(result.getResponse().getContentAsString()));
    }

    /**
     * Testing route PUT /medicalRecord with a malformed body
     *
     * @throws Exception Exception
     */
    @Test
    public void givenMalformedBody_whenUpdateMedicalRecord_thenReturnBadRequest() throws Exception {
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
                .header("X-API-VERSION", apiVersion)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body));

        // Then
        resultActions.andExpect(status().isBadRequest())
                .andDo(result -> System.out.println(result.getResponse().getContentAsString()));
    }

}
