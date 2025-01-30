package com.openclassrooms.SafetyNet.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.SafetyNet.config.JsonTestConfig;
import com.openclassrooms.SafetyNet.model.Person;
import com.openclassrooms.SafetyNet.utils.JsonFileManager;
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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class PersonControllerIT {

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
     * Test of getPersons method
     * - Given a list of persons
     * - Then OK and PersonList
     *
     * @throws Exception exception
     */
    @Test
    public void givenPersonList_whenGetPersons_thenOkAndPersonList() throws Exception {

        // Given

        // When
        ResultActions resultActions = mockMvc.perform(get("/persons")
                .header("X-API-VERSION", 1));

        // Then
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(24)));
    }

    /**
     * Test of getPersonInfo method
     * - Given an existing person
     * - Then OK and Person is deleted
     *
     * @throws Exception exception
     */
    @Test
    public void givenExistingPerson_whenDeletePersonByFirstNameAndLastName_thenOkAndPersonIsDeleted() throws Exception {
        // Given
        String firstName = "John";
        String lastName = "Boyd";

        // When
        ResultActions resultActions = mockMvc.perform(delete("/person?firstName={firstName}&lastName={lastName}", firstName, lastName)
                .header("X-API-VERSION", 1)
                .contentType(MediaType.APPLICATION_JSON));

        // Then
        resultActions.andExpect(status().isOk());
        assertTrue(jsonFileManager.getPersons()
                .stream()
                .noneMatch(p -> p.getFirstName().equals(firstName) && p.getLastName().equals(lastName))
        );
    }

    /**
     * Test of getPersonInfo method
     * - Given a non-existing person
     * - Then NOT FOUND
     *
     * @throws Exception exception
     */
    @Test
    public void givenNonExistingPerson_whenDeletePersonByFirstNameAndLastName_thenNotFound() throws Exception {
        // Given
        String firstName = "UnknownFirstName";
        String lastName = "UnknownLastName";

        // When
        ResultActions resultActions = mockMvc.perform(delete("/person?firstName={firstName}&lastName={lastName}", firstName, lastName)
                .header("X-API-VERSION", 1)
                .contentType(MediaType.APPLICATION_JSON));

        // Then
        resultActions.andExpect(status().isNotFound());
    }


    /**
     * Test of savePerson method
     * - Given a new person
     * - Then CREATED and Person is created
     *
     * @throws Exception exception
     */
    @Test
    public void givenNewPerson_whenSavePerson_thenCreatedAndPersonIsCreated() throws Exception {
        // Given
        Person pExpected = new Person("NewFirstName", "NewLastName", "New address", "New City", "99999", "999-999-9999", "newMail@email.com");
        ObjectMapper objectMapper = new ObjectMapper();
        String personToJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(pExpected);

        // When
        ResultActions resultActions = mockMvc.perform(post("/person")
                .header("X-API-VERSION", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(personToJson));

        // Then
        resultActions.andExpect(status().isCreated());
        Person pActual = jsonFileManager.getPersons()
                .stream()
                .filter(p -> p.getFirstName().equals(pExpected.getFirstName()) && p.getLastName().equals(pExpected.getLastName()))
                .findFirst()
                .orElse(null);
        assertEquals(pExpected, pActual);
    }

    /**
     * Test of savePerson method
     * - Given an existing person
     * - Then CONFLICT
     *
     * @throws Exception exception
     */
    @Test
    public void givenExistingPerson_whenSavePerson_thenConflict() throws Exception {
        // Given
        Person person = new Person("Jacob", "Boyd", "new address", "New City", "99999", "999-999-9999", "newMail@email.com");
        ObjectMapper objectMapper = new ObjectMapper();
        String personToJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(person);

        // When
        ResultActions resultActions = mockMvc.perform(post("/person")
                .header("X-API-VERSION", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(personToJson));

        // Then
        resultActions.andExpect(status().isConflict());
    }

    /**
     * Test of savePerson method
     * - Given a malformed body
     * - Then BAD REQUEST
     *
     * @throws Exception exception
     */
    @Test
    public void givenMalformedBody_whenSavePerson_thenBadRequest() throws Exception {
        // Given
        String body = "{\n" +
                "  \"firstName\": \"John\",\n" +
                "  \"lastName\": \"Boyd\",\n" +
                "  \"email\": \"mail@email.com\"\n" +
                "}";
        // When
        ResultActions resultActions = mockMvc.perform(post("/person")
                .header("X-API-VERSION", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body));

        // Then
        resultActions.andExpect(status().isBadRequest())
                .andDo(result -> System.out.println(result.getResponse().getContentAsString()));
    }

    /**
     * Test of updatePerson method
     * - Given an existing person
     * - Then Not Found
     *
     * @throws Exception exception
     */
    @Test
    public void givenNonExsitingPerson_whenUpdatePerson_thenNotFound() throws Exception {
        // Given
        Person person = new Person("UnknowFirstName", "UnknowLastName", "Update address", "Uew City", "99999", "999-999-9999", "UpdateMail@email.com");
        ObjectMapper objectMapper = new ObjectMapper();
        String personToJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(person);

        // When
        ResultActions resultActions = mockMvc.perform(put("/person")
                .header("X-API-VERSION", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(personToJson));

        // Then
        resultActions.andExpect(status().isNotFound());
    }

    /**
     * Test of updatePerson method
     * - Given an existing person
     * - Then OK and Person is updated
     *
     * @throws Exception exception
     */
    @Test
    public void givenExistingPerson_whenUpdatePerson_thenOkAndUpdatedPerson() throws Exception {
        // Given
        Person pExpected = new Person("John", "Boyd", "Update address", "Update City", "99999", "999-999-9999", "update@email.com");
        ObjectMapper objectMapper = new ObjectMapper();
        String personToJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(pExpected);

        // When
        ResultActions resultActions = mockMvc.perform(put("/person")
                .header("X-API-VERSION", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(personToJson));

        // Then
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value(pExpected.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(pExpected.getLastName()))
                .andExpect(jsonPath("$.address").value(pExpected.getAddress()))
                .andExpect(jsonPath("$.city").value(pExpected.getCity()))
                .andExpect(jsonPath("$.zip").value(pExpected.getZip()))
                .andExpect(jsonPath("$.phone").value(pExpected.getPhone()))
                .andExpect(jsonPath("$.email").value(pExpected.getEmail()));

        Person pActual = jsonFileManager.getPersons()
                .stream()
                .filter(p -> p.getFirstName().equals(pExpected.getFirstName()) && p.getLastName().equals(pExpected.getLastName()))
                .findFirst()
                .orElse(null);
        assertEquals(pExpected, pActual);
    }


    /**
     * Test of updatePerson method
     * - Given a malformed body
     * - Then BAD REQUEST
     *
     * @throws Exception exception
     */
    @Test
    public void givenMalformedBody_whenUpdatePerson_thenBadRequest() throws Exception {
        // Given
        String body = "{\n" +
                "  \"firstName\": \"John\",\n" +
                "  \"lastName\": \"Boyd\",\n" +
                "  \"address\": \"1509 Culver St\",\n" +
                "  \"city\": \"Culver\",\n" +
                "  \"email\": \"mail@email.com\"\n" +
                "}";

        // When
        ResultActions resultActions = mockMvc.perform(put("/person")
                .header("X-API-VERSION", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body));

        // Then
        resultActions.andExpect(status().isBadRequest())
                .andDo(result -> System.out.println(result.getResponse().getContentAsString()));
    }
}
