package com.openclassrooms.SafetyNet.repository;

import com.openclassrooms.SafetyNet.exceptions.JsonFileManagerSaveException;
import com.openclassrooms.SafetyNet.model.Person;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@Repository
public class PersonRepository {

    private final JsonFileManager jsonFileManager;

    /**
     * Constructor
     *
     * @param jsonFileManager JsonFileManager
     */
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
        Person personFound = getPersons().stream()
                .filter(p -> p.getFirstName().equals(firstName) && p.getLastName().equals(lastName))
                .findFirst()
                .orElse(null);
        log.debug("Person {} {} {}", firstName, lastName, personFound != null ? "found" : "not found");
        return personFound;
    }

    /**
     * Get a list of persons by last name
     *
     * @param lastName String case-sensitive
     * @return List of Person objects
     */
    public List<Person> getPersonByLastName(String lastName) {
        List<Person> persons = getPersons().stream()
                .filter(p -> p.getLastName().equals(lastName))
                .toList();
        log.debug("{} persons with last name {} found", persons.size(), lastName);
        return persons;
    }

    /**
     * Get a list of persons by address
     *
     * @param address String address of the person (case-sensitive)
     * @return List of Person objects
     */
    public List<Person> getPersonByAddress(String address) {

        List<Person> persons = getPersons().stream()
                .filter(p -> p.getAddress().equals(address))
                .toList();
        log.debug("{} persons with address {} found", persons.size(), address);
        return persons;
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
        // Ici, on récupère la référence et non une copie de la liste
        List<Person> persons = getPersons();
        boolean deleted = persons.removeIf(person -> person.getFirstName().equals(firstName) && person.getLastName().equals(lastName));
        if (deleted) {
            jsonFileManager.saveJsonFile();
        }
        log.debug("Person {} {} deleted : {}", firstName, lastName, deleted);
        return deleted;
    }

    /**
     * Save a person
     *
     * @param person Person
     * @throws JsonFileManagerSaveException if an error occurs while saving the file
     */
    public void savePerson(Person person) throws JsonFileManagerSaveException {
        // Ici, on récupère la référence et non une copie de la liste
        List<Person> persons = getPersons();
        persons.add(person);
        log.debug("Person {} {} saved", person.getFirstName(), person.getLastName());
        jsonFileManager.saveJsonFile();
    }


    /**
     * Update a person
     *
     * @param person Person object with the new information
     * @return Person object updated
     * @throws JsonFileManagerSaveException if an error occurs while saving the file
     */
    public Person updatePerson(Person person) throws JsonFileManagerSaveException {
        // Ici, on récupère la référence et non une copie de la liste
        Person existingPerson = getPersonByFirstNameAndLastName(person.getFirstName(), person.getLastName());
        if (existingPerson != null) {
            existingPerson.setAddress(person.getAddress());
            existingPerson.setCity(person.getCity());
            existingPerson.setZip(person.getZip());
            existingPerson.setPhone(person.getPhone());
            existingPerson.setEmail(person.getEmail());
            jsonFileManager.saveJsonFile();
            return existingPerson;
        }
        log.debug("Person {} {} not found", person.getFirstName(), person.getLastName());
        return null;
    }

}
