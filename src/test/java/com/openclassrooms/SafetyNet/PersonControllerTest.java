package com.openclassrooms.SafetyNet;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.SafetyNet.controller.PersonController;
import com.openclassrooms.SafetyNet.exceptions.ConflictException;
import com.openclassrooms.SafetyNet.exceptions.NotFoundException;
import com.openclassrooms.SafetyNet.model.Person;
import com.openclassrooms.SafetyNet.service.PersonService;
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
 * Tests for PersonController
 */
@WebMvcTest(controllers = PersonController.class)
public class PersonControllerTest {

    private final MockMvc mockMvc;
    private List<Person> persons;
    private final String apiVersion = "1";

    @MockitoBean
    private PersonService personService;

    /**
     * Constructor
     *
     * @param mockMvc MockMvc
     */
    @Autowired
    public PersonControllerTest(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }

    /**
     * Set up before each test
     */
    @BeforeEach
    public void setUpPerTest() {
        persons = Arrays.asList(
                new Person("John", "Boyd", "1509 Culver St", "Culver", "97451", "841-874-6512", "jaboyd@email.com"),
                new Person("Jacob", "Boyd", "1509 Culver St", "Culver", "97451", "841-874-6513", "drk@email.com"),
                new Person("Tenley", "Boyd", "1509 Culver St", "Culver", "97451", "841-874-6512", "tenz@email.com")
        );
    }

    /**
     * Testing route GET /persons
     * - Given list of persons
     * - Retrun a list of persons
     *
     * @throws Exception Exception
     */
    @Test
    public void givenPersonList_whenGetPersons_thenReturnPersonList() throws Exception {

        // Given
        when(personService.getPersons()).thenReturn(persons);

        // When
        ResultActions resultActions = mockMvc.perform(get("/persons")
                .header("X-API-VERSION", apiVersion)
                .contentType(MediaType.APPLICATION_JSON));

        // Then
        resultActions.andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(3)));
    }

    /**
     * Testing route GET /person with firstName and lastName as query parameters
     * - Given an existing person
     * - Then OK and the person
     *
     * @throws Exception Exception
     */
    @Test
    public void givenExistingPerson_whenGetPersonByFirstNameAndLastName_thenReturnOkAndPerson() throws Exception {
        // Given
        String firstName = "John";
        String lastName = "Boyd";

        when(personService.getPersonByFirstNameAndLastName(firstName, lastName)).thenReturn(persons.getFirst());

        // When
        ResultActions resultActions = mockMvc.perform(get("/person?firstName={firstName}&lastName={lastName}", firstName, lastName)
                .header("X-API-VERSION", apiVersion)
                .contentType(MediaType.APPLICATION_JSON));

        // Then
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Boyd"));
    }

    /**
     * Testing route GET /person with firstName and lastName as query parameters
     * - Given a non-existing person
     * - Then NotFound
     *
     * @throws Exception Exception
     */
    @Test
    public void givenNonExistingPerson_whenGetPersonByFirstNameAndLastName_thenReturnNotFound() throws Exception {
        // Given
        String firstName = "UnknownFirstName";
        String lastName = "UnknownLastName";

        when(personService.getPersonByFirstNameAndLastName(firstName, lastName)).
                thenThrow(new NotFoundException("Person not found with firstName: " + firstName + " and lastName: " + lastName));

        // When
        ResultActions resultActions = mockMvc.perform(get("/person?firstName={firstName}&lastName={lastName}", firstName, lastName)
                .header("X-API-VERSION", apiVersion)
                .contentType(MediaType.APPLICATION_JSON));

        // Then
        resultActions.andExpect(status().isNotFound());

    }


    /**
     * Testing route DELETE /person with firstName and lastName as query parameters
     * - Given an existing person
     * - Then OK
     *
     * @throws Exception Exception
     */
    @Test
    public void givenExistingPerson_whenDeletePersonByFirstNameAndLastName_thenReturnOk() throws Exception {
        // Given
        String firstName = "John";
        String lastName = "Boyd";

        doNothing().when(personService).deletePersonByFirstNameAndLastName(firstName, lastName);

        // When
        ResultActions resultActions = mockMvc.perform(delete("/person?firstName={firstName}&lastName={lastName}", firstName, lastName)
                .header("X-API-VERSION", apiVersion)
                .contentType(MediaType.APPLICATION_JSON));

        // Then
        resultActions.andExpect(status().isOk());
    }

    /**
     * Testing route DELETE /person with firstName and lastName as query parameters
     * - Given a non-existing person
     * - Then NotFound
     *
     * @throws Exception Exception
     */
    @Test
    public void givenNonExistingPerson_whenDeletePersonByFirstNameAndLastName_thenReturnNotFound() throws Exception {
        // Given
        String firstName = "UnknownFirstName";
        String lastName = "UnknownLastName";

        doThrow(new NotFoundException("Person not found with firstName: " + firstName + " and lastName: " + lastName))
                .when(personService).deletePersonByFirstNameAndLastName(firstName, lastName);

        // When
        ResultActions resultActions = mockMvc.perform(delete("/person?firstName={firstName}&lastName={lastName}", firstName, lastName)
                .header("X-API-VERSION", apiVersion)
                .contentType(MediaType.APPLICATION_JSON));

        // Then
        resultActions.andExpect(status().isNotFound());
    }


    /**
     * Testing route POST /person
     * - Given a new person
     * - Then Created
     *
     * @throws Exception Exception
     */
    @Test
    public void givenNewPerson_whenSavePerson_thenReturnCreated() throws Exception {
        // Given
        Person person = new Person("NewFirstName", "NewLastName", "new address", "New City", "99999", "999-999-9999", "newMail@email.com");
        ObjectMapper objectMapper = new ObjectMapper();
        String personToJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(person);

        doNothing().when(personService).savePerson(person);

        // When
        ResultActions resultActions = mockMvc.perform(post("/person")
                .header("X-API-VERSION", apiVersion)
                .contentType(MediaType.APPLICATION_JSON)
                .content(personToJson));

        // Then
        resultActions.andExpect(status().isCreated());
    }

    /**
     * Testing route POST /person
     * - Given an existing person
     * - Then Conflict
     *
     * @throws Exception Exception
     */
    @Test
    public void givenExistingPerson_whenSavePerson_thenReturnConflict() throws Exception {
        // Given
        Person person = new Person("Jacob", "Boyd", "new address", "New City", "99999", "999-999-9999", "newMail@email.com");
        ObjectMapper objectMapper = new ObjectMapper();
        String personToJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(person);

        doThrow(new ConflictException("Person already exists with firstName: " + person.getFirstName() + " and lastName: " + person.getLastName()))
                .when(personService).savePerson(person);

        // When
        ResultActions resultActions = mockMvc.perform(post("/person")
                .header("X-API-VERSION", apiVersion)
                .contentType(MediaType.APPLICATION_JSON)
                .content(personToJson));

        // Then
        resultActions.andExpect(status().isConflict());
    }

    /**
     * Testing route POST /person
     * - Given a malformed body
     * - Then BadRequest
     *
     * @throws Exception Exception
     */
    @Test
    public void givenMalformedBody_whenSavePerson_thenReturnBadRequest() throws Exception {
        // Given
        String body = "{\n" +
                "  \"firstName\": \"John\",\n" +
                "  \"lastName\": \"Boyd\",\n" +
                "  \"email\": \"mail@email.com\"\n" +
                "}";
        // When
        ResultActions resultActions = mockMvc.perform(post("/person")
                .header("X-API-VERSION", apiVersion)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body));

        // Then
        resultActions.andExpect(status().isBadRequest())
                .andDo(result -> System.out.println(result.getResponse().getContentAsString()));
    }


    /**
     * Testing route PUT /person
     * - Given a non-existing person
     * - Then NotFound
     *
     * @throws Exception Exception
     */
    @Test
    public void givenNonExsitingPerson_whenUpdatePerson_thenReturnNotFound() throws Exception {
        // Given
        Person person = new Person("UnknowFirstName", "UnknowLastName", "Update address", "Uew City", "99999", "999-999-9999", "UpdateMail@email.com");
        ObjectMapper objectMapper = new ObjectMapper();
        String personToJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(person);

        when(personService.updatePerson(person))
                .thenThrow(new NotFoundException("Person not found with firstName: " + person.getFirstName() + " and lastName: " + person.getLastName()));

        // When
        ResultActions resultActions = mockMvc.perform(put("/person")
                .header("X-API-VERSION", apiVersion)
                .contentType(MediaType.APPLICATION_JSON)
                .content(personToJson));

        // Then
        resultActions.andExpect(status().isNotFound());


    }

    /**
     * Testing route PUT /person
     * - Given an existing person
     * - Then OK and the updated person
     *
     * @throws Exception Exception
     */
    @Test
    public void givenPerson_whenUpdatePerson_thenReturnOkAndUpdatedPerson() throws Exception {
        // Given
        Person person = new Person("John", "Boyd", "Update address", "Update City", "99999", "999-999-9999", "update@email.com");
        ObjectMapper objectMapper = new ObjectMapper();
        String personToJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(person);

        when(personService.updatePerson(person)).thenReturn(person);

        // When
        ResultActions resultActions = mockMvc.perform(put("/person")
                .header("X-API-VERSION", apiVersion)
                .contentType(MediaType.APPLICATION_JSON)
                .content(personToJson));

        // Then
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Boyd"))
                .andExpect(jsonPath("$.address").value("Update address"))
                .andExpect(jsonPath("$.city").value("Update City"))
                .andExpect(jsonPath("$.zip").value("99999"))
                .andExpect(jsonPath("$.phone").value("999-999-9999"))
                .andExpect(jsonPath("$.email").value("update@email.com"));
    }

    /**
     * Testing route PUT /person
     * - Given a malformed body
     * - Then BadRequest
     *
     * @throws Exception Exception
     */
    @Test
    public void givenMalformedBody_whenUpdatePerson_thenReturnBadRequest() throws Exception {
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
                .header("X-API-VERSION", apiVersion)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body));

        // Then
        resultActions.andExpect(status().isBadRequest())
                .andDo(result -> System.out.println(result.getResponse().getContentAsString()));
    }

}
