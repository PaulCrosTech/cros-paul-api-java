package com.openclassrooms.SafetyNet.repository;

import com.openclassrooms.SafetyNet.exceptions.JsonFileManagerSaveException;
import com.openclassrooms.SafetyNet.model.Person;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Log4j2
@Repository
public class PersonRepository {

    private final JsonFileManager jsonFileManager;

    @Autowired
    public PersonRepository(JsonFileManager jsonFileManager) {
        log.info("<constructor> PersonRepository");
        this.jsonFileManager = jsonFileManager;
    }

    /**
     * Get all persons
     *
     * @return List of Person objects
     */
    public List<Person> getPersons() {
        log.info("<repo> getPersons");
        return jsonFileManager.getPersons();
    }

    /**
     * Get a person by first name and last name
     *
     * @param firstName String
     * @param lastName  String
     * @return Person object
     */
    public Person getPersonByFirstNameAndLastName(String firstName, String lastName) {
        log.info("<repo> getPersonByFirstnameAndLastname : firstName: {} and lastName: {}", firstName, lastName);
        for (Person person : getPersons()) {
            if (person.getFirstName().equals(firstName) && person.getLastName().equals(lastName)) {
                log.info("<repo> getPersonByFirstnameAndLastname : person found");
                return person;
            }
        }
        return null;
    }

    /**
     * Delete a person by first name and last name
     *
     * @param firstName String case-sensitive
     * @param lastName  String case-sensitive
     * @return boolean if a person is deleted
     * @throws JsonFileManagerSaveException if an error occurs while saving the file
     */
    public boolean deletePersonByFirstNameAndLastName(String firstName, String lastName) throws JsonFileManagerSaveException {
        log.info("<repo> deletePersonByFirstnameAndLastname : firstName: {} and lastName: {}", firstName, lastName);
        List<Person> persons = getPersons();
        boolean deleted = persons.removeIf(person -> person.getFirstName().equals(firstName) && person.getLastName().equals(lastName));
        if (deleted) {
            log.info("<repo> deletePersonByFirstnameAndLastname : person deleted");
            jsonFileManager.saveJsonFile();
        }
        return deleted;
    }

}
