package com.openclassrooms.SafetyNet.repository;

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
        this.jsonFileManager = jsonFileManager;
    }

    /**
     * @return List of Person objects
     */
    public List<Person> getPersons() {
        log.debug("==> Repository : getPersons");
        return jsonFileManager.getPersons();
    }

    /**
     * @param firstName String
     * @param lastName  String
     * @return Person object
     */
    public Person getPersonByFirstnameAndLastname(String firstName, String lastName) {
        for (Person person : getPersons()) {
            if (person.getFirstName().equals(firstName) && person.getLastName().equals(lastName)) {
                return person;
            }
        }
        return null;
    }
}
