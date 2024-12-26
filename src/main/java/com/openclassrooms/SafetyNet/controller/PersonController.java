package com.openclassrooms.SafetyNet.controller;

import com.openclassrooms.SafetyNet.model.Person;
import com.openclassrooms.SafetyNet.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class PersonController {

    @Autowired
    private PersonService personService;

    @GetMapping("/person")
    public List<Person> getPersons() {
        return personService.getPersons();
    }

    @GetMapping("/person/{firstName}/{lastName}")
    public Person getPersonByFirstnameAndLastName(@PathVariable String firstName, @PathVariable String lastName) {
        return personService.getPersonByFirstnameAndLastname(firstName, lastName);
    }
}
