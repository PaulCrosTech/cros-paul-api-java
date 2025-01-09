package com.openclassrooms.SafetyNet.integration;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.SafetyNet.model.JsonModel;
import com.openclassrooms.SafetyNet.model.Person;
import com.openclassrooms.SafetyNet.repository.JsonFileManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


// TODO : pas de test d'intégration, faire des tests unitaires et mock les services
//@WebMvcTest(controllers = PersonController.class)
@SpringBootTest
@AutoConfigureMockMvc
public class PersonControllerTestIT {


    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;
//
//    @Mock
//    private JsonFileManager jsonFileManager;

    @Autowired
    public PersonControllerTestIT(MockMvc mockMvc, ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.mockMvc = mockMvc;
    }

//    @MockitoBean
//    private PersonService personService;


//    @BeforeEach
//    public void setUp() throws IOException {
//        File jsonFile = new File("src/test/java/com/openclassrooms/SafetyNet/integration/ressources/datasIT.json");
//        List<JsonModel> json = objectMapper.readValue(jsonFile, new TypeReference<>() {
//        });
//        when(jsonFileManager.getPersons()).thenReturn(json.get);
//    }

    /**
     * Testing route /person
     *
     * @throws Exception Exception
     */
    @Test
    public void testGetAllPersons() throws Exception {
//        when(personService.getPersons()).thenReturn(new ArrayList<>());

        MvcResult result = mockMvc.perform(get("/person").header("X-API-VERSION", "1"))
                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.length()").value(23))
                .andReturn();


        String json = result.getResponse().getContentAsString();
        List<Person> personList = objectMapper.readValue(json, new TypeReference<>() {
        });

        assertNotNull(personList);
        assertEquals(23, personList.size());

    }

    /**
     * Testing route /person/{firstName}/{lastName} with an existing person
     *
     * @throws Exception Exception
     */
    @Test
    public void givenExistingPerson_whenGetPersonByFirstNameAndLastName_thenReturnsPerson() throws Exception {
        mockMvc.perform(get("/person/John/Boyd").header("X-API-VERSION", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Boyd"));
    }

    /**
     * Testing route /person/{firstName}/{lastName} with a non-existing person
     *
     * @throws Exception Exception
     */
    @Test
    public void givenNonExistingPerson_whenGetPersonByFirstNameAndLastName_thenReturnsPerson() throws Exception {
        mockMvc.perform(get("/person/John/Doe").header("X-API-VERSION", "1"))
                .andExpect(status().isNotFound());
    }

    /**
     * Testing route /person/{firstName}/{lastName} with an existing person
     *
     * @throws Exception Exception
     */
    @Test
    public void givenExistingPerson_whenDeletePerson_thenRetun200() throws Exception {
        mockMvc.perform(delete("/person/John/Boyd").header("X-API-VERSION", "1"))
                .andExpect(status().isOk());
    }

    /**
     * Testing route /person/{firstName}/{lastName} with a non-existing person
     *
     * @throws Exception Exception
     */
    @Test
    public void givenNonExistingPerson_whenDeletePerson_thenRetun200() throws Exception {
        mockMvc.perform(delete("/person/John/Doe").header("X-API-VERSION", "1"))
                .andExpect(status().isNotFound());
    }

}
