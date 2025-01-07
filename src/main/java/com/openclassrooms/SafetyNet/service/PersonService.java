package com.openclassrooms.SafetyNet.service;

import com.openclassrooms.SafetyNet.exceptions.JsonFileManagerSaveException;
import com.openclassrooms.SafetyNet.exceptions.ConflictException;
import com.openclassrooms.SafetyNet.exceptions.NotFoundException;
import com.openclassrooms.SafetyNet.dto.PersonUpdateDTO;
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
        log.info("<constructor> PersonService");
        this.personRepository = personRepository;
    }

    /**
     * Get all persons
     *
     * @return List of Person objects
     */
    public List<Person> getPersons() {
        log.info("<service> getPersons");
        return personRepository.getPersons();
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
        log.info("<service> getPersonByFirstnameAndLastname : firstName: {} and lastName: {}", firstName, lastName);
        Person person = personRepository.getPersonByFirstNameAndLastName(firstName, lastName);
        if (person == null) {
            log.info("<service> Person not found");
            throw new NotFoundException("Person not found with firstName: " + firstName + " and lastName: " + lastName);
        }
        log.info("<service> Person found");
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
        log.info("<service> deletePersonByFirstNameAndLastName : firstName: {} and lastName: {}", firstName, lastName);
        try {
            boolean deleted = personRepository.deletePersonByFirstNameAndLastName(firstName, lastName);
            if (!deleted) {
                log.info("<service> Person not found");
                throw new NotFoundException("Person not found with firstName: " + firstName + " and lastName: " + lastName);
            }
        } catch (JsonFileManagerSaveException e) {
            log.info("<service> Error while deleting in JSON file");
            throw new Exception("Error while deleting the person in JSON file, firstName: " + firstName + " and lastName: " + lastName);
        }
        log.info("<service> Person deleted");
    }


    /**
     * Save a person
     *
     * @param person Person
     */
    public void savePerson(Person person) throws JsonFileManagerSaveException, ConflictException {
        log.info("<service> savePerson");

        try {
            // Vérifie si la personne existe déjà
            Person personExist = getPersonByFirstNameAndLastName(person.getFirstName(), person.getLastName());
            if (personExist != null) {
                log.info("<service> Person already exist");
                throw new ConflictException("Person already exist with firstName: " + person.getFirstName() + " and lastName: " + person.getLastName());
            }
        } catch (NotFoundException e) {
            // Création de la personne
            try {
                personRepository.savePerson(person);
            } catch (JsonFileManagerSaveException ex) {
                log.info("<service> Error while saving in JSON file");
                throw new JsonFileManagerSaveException("Error while saving the person in JSON file");
            }
            log.info("<service> Person saved");
        }
    }


    /**
     * Update a person
     *
     * @param firstName first name of the person to be updated
     * @param lastName  last name of the person to be updated
     * @param person    PersonUpdateDTO object with the new information
     * @return Person object updated
     * @throws NotFoundException if person not found
     */
    public Person updatePerson(String firstName, String lastName, PersonUpdateDTO person) throws NotFoundException {
        log.info("<service> updatePerson");

        Person personUpdated = personRepository.updatePerson(firstName, lastName, person);
        if (personUpdated == null) {
            log.info("<service> Person not found");
            throw new NotFoundException("Person not found with firstName: " + firstName + " and lastName: " + lastName);
        }

        log.info("<service> Person updated");

        return personUpdated;
    }
}
