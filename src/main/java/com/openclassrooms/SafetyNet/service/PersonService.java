package com.openclassrooms.SafetyNet.service;

import com.openclassrooms.SafetyNet.repository.PersonRepository;
import com.openclassrooms.SafetyNet.model.Person;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Log4j2
@Service
@Data
public class PersonService {

    private final PersonRepository personRepository;

    @Autowired
    public PersonService(PersonRepository personRepository) {
        log.info("==> PersonService : constructor");
        this.personRepository = personRepository;
    }

    /**
     * @return List of Person objects
     */
    public List<Person> getPersons() {
        log.info("==> PersonService : getPersons");
        return personRepository.getPersons();
    }

    /**
     * @param firstName String
     * @param lastName  String
     * @return Person object
     */
    public Person getPersonByFirstnameAndLastname(String firstName, String lastName) {
        log.info("==> PersonService : getPersonByFirstnameAndLastname");
        return personRepository.getPersonByFirstnameAndLastname(firstName, lastName);
    }

    /**
     * @param firstName String case-sensitive
     * @param lastName  String case-sensitive
     * @return boolean
     */
    public boolean deletePersonByFirstnameAndLastname(String firstName, String lastName) {
        log.info("==> PersonService : deletePersonByFirstnameAndLastname");
        return personRepository.deletePersonByFirstnameAndLastname(firstName, lastName);
    }
}
