package com.openclassrooms.SafetyNet.repository;

import com.openclassrooms.SafetyNet.model.Person;
import com.openclassrooms.SafetyNet.utils.JsonManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class PersonRepository {

    @Autowired
    private JsonManager jsonManager;

    public List<Person> getPersons() {
        return jsonManager.getPersons();
    }

    public Person getPersonByFirstnameAndLastname(String firstName, String lastName) {
        System.out.println("getPersonByFirstnameAndLastname " + firstName + " " + lastName);
        for (Person person : getPersons()) {
            System.out.println(" --> " + person.getFirstName() + " " + person.getLastName());
            if (person.getFirstName().equals(firstName) && person.getLastName().equals(lastName)) {
                System.out.println("!! Trouvée !! ");
                return person;
            }
        }
        return null;
    }
}
