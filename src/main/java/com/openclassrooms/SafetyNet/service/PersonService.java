package com.openclassrooms.SafetyNet.service;

import com.openclassrooms.SafetyNet.exceptions.JsonFileManagerSaveException;
import com.openclassrooms.SafetyNet.exceptions.ConflictException;
import com.openclassrooms.SafetyNet.exceptions.NotFoundException;
import com.openclassrooms.SafetyNet.repository.PersonRepository;
import com.openclassrooms.SafetyNet.model.Person;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.List;

@Log4j2
@Service
@Data
public class PersonService {

    private final PersonRepository personRepository;

    public PersonService(PersonRepository personRepository) {
        log.info("<constructor> PersonService");
        this.personRepository = personRepository;
    }

    /**
     * Get all persons
     *
     * @return List of Person objects
     */
    public List<Person> getPersons() {
        List<Person> persons = personRepository.getPersons();
        log.info("{} persons found", persons.size());
        return persons;
    }

    /**
     * Get a person by first name and last name
     *
     * @param firstName String case-sensitive
     * @param lastName  String case-sensitive
     * @return Person object
     * @throws NotFoundException if person not found
     */
    public Person getPersonByFirstNameAndLastName(String firstName, String lastName) throws NotFoundException {
        Person person = personRepository.getPersonByFirstNameAndLastName(firstName, lastName);
        if (person == null) {
            throw new NotFoundException("Person not found with firstName: " + firstName + " and lastName: " + lastName);
        }
        log.info("Person {} {} found", firstName, lastName);
        return person;
    }

    /**
     * Delete a person by first name and last name
     *
     * @param firstName String case-sensitive
     * @param lastName  String case-sensitive
     * @throws Exception if an error occurs while deleting the person
     */
    public void deletePersonByFirstNameAndLastName(String firstName, String lastName) throws Exception {
        try {
            boolean deleted = personRepository.deletePersonByFirstNameAndLastName(firstName, lastName);
            if (!deleted) {
                throw new NotFoundException("Person not found with firstName: " + firstName + " and lastName: " + lastName);
            }
        } catch (JsonFileManagerSaveException e) {
            throw new Exception("Error while deleting the person in JSON file, firstName: " + firstName + " and lastName: " + lastName);
        }
        log.info("Person {} {} deleted", firstName, lastName);
    }


    /**
     * Save a person
     *
     * @param person Person
     */
    public void savePerson(Person person) throws JsonFileManagerSaveException, ConflictException {
        try {
            // Vérifie si la personne existe déjà
            Person personExist = getPersonByFirstNameAndLastName(person.getFirstName(), person.getLastName());
            if (personExist != null) {
                throw new ConflictException("Person already exist with firstName: " + person.getFirstName() + " and lastName: " + person.getLastName());
            }
        } catch (NotFoundException e) {
            // Création de la personne
            try {
                personRepository.savePerson(person);
            } catch (JsonFileManagerSaveException ex) {
                throw new JsonFileManagerSaveException("Error while saving the person in JSON file");
            }
            log.info("Person {} {} saved", person.getFirstName(), person.getLastName());
        }
    }


    /**
     * Update a person
     *
     * @param person Person object with the new information
     * @return Person object updated
     * @throws NotFoundException if person not found
     */
    public Person updatePerson(Person person) throws NotFoundException {
        Person personUpdated = personRepository.updatePerson(person);
        if (personUpdated == null) {
            throw new NotFoundException("Person not found with firstName: " + person.getFirstName() + " and " + person.getLastName());
        }
        log.info("Person {} {} updated", person.getFirstName(), person.getLastName());
        return personUpdated;
    }
}
