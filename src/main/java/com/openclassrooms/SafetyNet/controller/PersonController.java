package com.openclassrooms.SafetyNet.controller;

import com.openclassrooms.SafetyNet.model.Person;
import com.openclassrooms.SafetyNet.service.PersonService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
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
     *
     * @return List of Person objects
     */
    @GetMapping("/person")
    public List<Person> getPersons() {
        log.info("==> Request GET on /person");
        return personService.getPersons();
    }

    /**
     * Get a person by first name and last name
     *
     * @param firstName String case-sensitive
     * @param lastName  String case-sensitive
     * @return Person object
     */
    @GetMapping("/person/{firstName}/{lastName}")
    public Person getPersonByFirstnameAndLastName(@PathVariable String firstName, @PathVariable String lastName) {
        log.info("==> Request GET on /person/{}/{}", firstName, lastName);
        return personService.getPersonByFirstnameAndLastname(firstName, lastName);
    }

    /**
     * Delete a person by first name and last name
     *
     * @param firstName String case-sensitive
     * @param lastName  String case-sensitive
     */
    @DeleteMapping("/person/{firstName}/{lastName}")
    public HttpEntity<Object> deletePerson(@PathVariable String firstName, @PathVariable String lastName) {
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
