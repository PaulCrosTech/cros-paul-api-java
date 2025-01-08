package com.openclassrooms.SafetyNet.repository;

import com.openclassrooms.SafetyNet.exceptions.JsonFileManagerSaveException;
import com.openclassrooms.SafetyNet.model.Person;
import com.openclassrooms.SafetyNet.dto.PersonUpdateDTO;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
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
        for (Person person : getPersons()) {
            if (person.getFirstName().equals(firstName) && person.getLastName().equals(lastName)) {
                log.debug("Person {} {} found", firstName, lastName);
                return person;
            }
        }
        log.debug("Person {} {} not found", firstName, lastName);
        return null;
    }

    /**
     * Get a list of persons by last name
     *
     * @param lastName String case-sensitive
     * @return List of Person objects
     */
    public List<Person> getPersonByLastName(String lastName) {
        List<Person> persons = new ArrayList<>();

        for (Person p : getPersons()) {
            if (p.getLastName().equals(lastName)) {
                persons.add(p);
            }
        }
        log.debug("Persons with last name {} found", lastName);
        return persons;
    }

    /**
     * Get a list of persons by address
     *
     * @param address String address of the person (case-sensitive)
     * @return List of Person objects
     */
    public List<Person> getPersonByAddress(String address) {
        List<Person> persons = new ArrayList<>();

        for (Person person : getPersons()) {
            if (person.getAddress().equals(address)) {
                persons.add(person);
            }
        }
        log.debug("Persons with address {} found", address);
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
     * @param firstName first name of the person to update
     * @param lastName  last name of the person to update
     * @param person    PersonUpdateDTO object with the new information
     * @return Person object updated
     * @throws JsonFileManagerSaveException if an error occurs while saving the file
     */
    public Person updatePerson(String firstName, String lastName, PersonUpdateDTO person) throws JsonFileManagerSaveException {
        // Ici, on récupère la référence et non une copie de la liste
        Person existingPerson = getPersonByFirstNameAndLastName(firstName, lastName);
        if (existingPerson != null) {
            existingPerson.setAddress(person.getAddress());
            existingPerson.setCity(person.getCity());
            existingPerson.setZip(person.getZip());
            existingPerson.setPhone(person.getPhone());
            existingPerson.setEmail(person.getEmail());
            jsonFileManager.saveJsonFile();
            return existingPerson;
        }
        log.debug("Person {} {} not found", firstName, lastName);
        return null;
    }

}
