package com.openclassrooms.SafetyNet.controller;

import com.openclassrooms.SafetyNet.exceptions.CustomApiError;
import com.openclassrooms.SafetyNet.model.Person;
import com.openclassrooms.SafetyNet.model.PersonUpdateDTO;
import com.openclassrooms.SafetyNet.service.PersonService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
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
     *
     * @return List of Person objects
     */
    @Operation(summary = "Get all person", description = "Returns all persons")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved"),
    })
    @GetMapping(path = "/person", headers = "X-API-VERSION=1")
    public List<Person> getPersons() {
        log.info("<controller> **New** Request GET on /person (Version 1)");
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
    @Parameters({
            @Parameter(name = "firstName", description = "The first name of the person", required = true, example = "John"),
            @Parameter(name = "lastName", description = "The last name of the person", required = true, example = "Boyd")
    })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved"),
            @ApiResponse(responseCode = "404", description = "Not found - The person was not found", content = @Content)
    })
    @GetMapping(path = "/person/{firstName}/{lastName}", headers = "X-API-VERSION=1")
    public Person getPersonByFirstNameAndLastName(@PathVariable String firstName, @PathVariable String lastName) {
        log.info("<controller> **New** Request GET on /person/{}/{}", firstName, lastName);
        return personService.getPersonByFirstNameAndLastName(firstName, lastName);
    }

    /**
     * Delete a person by first name and last name
     *
     * @param firstName String case-sensitive
     * @param lastName  String case-sensitive
     */
    @Operation(summary = "Delete a person by first name and last name", description = "Delete a person by his first name and last name.<br>Names are case-sensitive")
    @Parameters({
            @Parameter(name = "firstName", description = "The first name of the person", required = true, example = "John"),
            @Parameter(name = "lastName", description = "The last name of the person", required = true, example = "Boyd")
    })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully deleted"),
            @ApiResponse(responseCode = "404", description = "Not found - The person was not found")
    })
    @DeleteMapping(path = "/person/{firstName}/{lastName}", headers = "X-API-VERSION=1")
    public void deletePerson(@PathVariable String firstName, @PathVariable String lastName) throws Exception {
        log.info("<controller> **New** Request DELETE on /person/{}/{}", firstName, lastName);
        personService.deletePersonByFirstNameAndLastName(firstName, lastName);
    }

    /**
     * Create - Add a new person
     *
     * @param person An object person
     * @return The location of person object saved
     */
    @Operation(summary = "Create a person", description = "Add a new person")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201",
                    description = "Successfully created",
                    content = @Content
            ),
            @ApiResponse(responseCode = "400",
                    description = "Bad request - The request is invalid",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CustomApiError.class))),
            @ApiResponse(responseCode = "409",
                    description = "Conflict - The person already exists",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CustomApiError.class))
            )
    })
    @PostMapping(path = "/person", headers = "X-API-VERSION=1")
    public ResponseEntity<Object> createPerson(@Valid @RequestBody Person person) {
        log.info("<controller> **New** Request POST on /person {}", person);

        personService.savePerson(person);

        // Create the location of the person object saved
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{firstName}/{lastName}")
                .buildAndExpand(person.getFirstName(), person.getLastName())
                .toUri();
        return ResponseEntity.created(location).build();
    }


    /**
     * Update a person
     *
     * @param firstName firstName of the person to update
     * @param lastName  lastName of the person to update
     * @param person    PersonUpdateDTO object to update
     * @return Person object
     */
    @Operation(summary = "Update a person", description = "Update a person by his first name and last name.<br>Names are case-sensitive")
    @Parameters({
            @Parameter(name = "firstName", description = "The first name of the person", required = true, example = "John"),
            @Parameter(name = "lastName", description = "The last name of the person", required = true, example = "Boyd")
    })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated"),
            @ApiResponse(responseCode = "404", description = "Not found - The person was not found", content = @Content)
    })
    @PutMapping(path = "/person/{firstName}/{lastName}", headers = "X-API-VERSION=1")
    public Person updatePerson(@PathVariable String firstName, @PathVariable String lastName, @Valid @RequestBody PersonUpdateDTO person) {
        log.info("<controller> **New** Request PUT on /person {}", person);

        return personService.updatePerson(firstName, lastName, person);
    }
}
