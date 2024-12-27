package com.openclassrooms.SafetyNet.controller;

import com.openclassrooms.SafetyNet.model.Person;
import com.openclassrooms.SafetyNet.service.PersonService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
        log.info("==> PersonController : constructor");
        this.personService = personService;
    }

    /**
     * Get all persons
     * Api version : 1
     *
     * @return List of Person objects
     */
    @GetMapping(path = "/person", headers = "X-API-VERSION=1")
    public List<Person> getPersons() {
        log.info("==> Request GET on /person (Version 1)");
        return personService.getPersons();
    }

    /**
     * Get all persons
     * Api version : 2
     *
     * @return List of Person objects
     */
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
    @GetMapping(path = "/person/{firstName}/{lastName}", headers = "X-API-VERSION=1")
    public ResponseEntity<Person> getPersonByFirstnameAndLastName(@PathVariable String firstName, @PathVariable String lastName) {
        log.info("==> Request GET on /person/{}/{}", firstName, lastName);
        Person person = personService.getPersonByFirstnameAndLastname(firstName, lastName);
        if (person == null) {
            log.error("==> Person {} {} not found", firstName, lastName);
            return ResponseEntity.notFound().build();
        } else {
            log.info("==> Person {} {} found", firstName, lastName);
            return ResponseEntity.ok(person);
        }
    }

    /**
     * Delete a person by first name and last name
     *
     * @param firstName String case-sensitive
     * @param lastName  String case-sensitive
     */
    @DeleteMapping(path = "/person/{firstName}/{lastName}", headers = "X-API-VERSION=1")
    public ResponseEntity<Object> deletePerson(@PathVariable String firstName, @PathVariable String lastName) {
        log.info("==> Request DELETE on /person/{}/{}", firstName, lastName);
        boolean deleted = personService.deletePersonByFirstnameAndLastname(firstName, lastName);

        if (deleted) {
            log.info("==> Person {} {} deleted", firstName, lastName);
            return ResponseEntity.ok().build();

        } else {
            log.error("==> Person {} {} not found", firstName, lastName);
            return ResponseEntity.notFound().build();
        }
    }
}
