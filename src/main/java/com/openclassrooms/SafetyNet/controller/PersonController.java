package com.openclassrooms.SafetyNet.controller;

import com.openclassrooms.SafetyNet.model.Person;
import com.openclassrooms.SafetyNet.service.PersonService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Person", description = "Person API")
@Log4j2
@RestController
public class PersonController {

    private final PersonService personService;


    /**
     * Constructor
     *
     * @param personService PersonService
     */
    @Autowired
    public PersonController(PersonService personService) {
        log.info("<constructor> PersonController");
        this.personService = personService;
    }

    /**
     * Get all persons
     * Api version : 1
     *
     * @return List of Person objects
     */
    @Operation(summary = "Get all person", description = "Returns all persons")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved"),
    })
    @GetMapping(path = "/person", headers = "X-API-VERSION=1")
    public List<Person> getPersons() {
        log.info("<controller> New Request GET on /person (Version 1)");
        return personService.getPersons();
    }

    /**
     * Get all persons
     * Api version : 2
     *
     * @return List of Person objects
     */
    @Operation(summary = "Get all person", description = "Returns all persons")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved"),
    })
    @GetMapping(path = "/person", headers = "X-API-VERSION=2")
    public List<Person> getPersonsV2() {
        log.info("==> Request GET on /person (Version 2)");
        return personService.getPersons();
    }


    /**
     * Get a person by first name and last name
     *
     * @param firstName String case-sensitive
     * @param lastName  String case-sensitive
     * @return Person object
     */
    @Operation(summary = "Get a person by first name and last name", description = "Returns a person by his first name and last name.<br>Names are case-sensitive")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved"),
            @ApiResponse(responseCode = "404", description = "Not found - The person was not found")
    })
    @Parameters({
            @Parameter(name = "firstName", description = "The first name of the person", required = true, example = "John"),
            @Parameter(name = "lastName", description = "The last name of the person", required = true, example = "Boyd")
    })
    @GetMapping(path = "/person/{firstName}/{lastName}", headers = "X-API-VERSION=1")
    public Person getPersonByFirstNameAndLastName(@PathVariable String firstName, @PathVariable String lastName) {
        log.info("<controller> New Request GET on /person/{}/{}", firstName, lastName);
        return personService.getPersonByFirstNameAndLastName(firstName, lastName);
    }

    /**
     * Delete a person by first name and last name
     *
     * @param firstName String case-sensitive
     * @param lastName  String case-sensitive
     */
    @Operation(summary = "Delete a person by first name and last name", description = "Delete a person by his first name and last name.<br>Names are case-sensitive")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully deleted"),
            @ApiResponse(responseCode = "404", description = "Not found - The person was not found")
    })
    @Parameters({
            @Parameter(name = "firstName", description = "The first name of the person", required = true, example = "John"),
            @Parameter(name = "lastName", description = "The last name of the person", required = true, example = "Boyd")
    })
    @DeleteMapping(path = "/person/{firstName}/{lastName}", headers = "X-API-VERSION=1")
    public void deletePerson(@PathVariable String firstName, @PathVariable String lastName) throws Exception {
        log.info("<controller> New Request DELETE on /person/{}/{}", firstName, lastName);
        personService.deletePersonByFirstNameAndLastName(firstName, lastName);
    }
}
