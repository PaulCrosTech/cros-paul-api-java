package com.openclassrooms.SafetyNet.service;

import com.openclassrooms.SafetyNet.repository.PersonRepository;
import com.openclassrooms.SafetyNet.model.Person;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Data
public class PersonService {
    @Autowired
    private PersonRepository personRepository;

    public List<Person> getPersons() {
        return personRepository.getPersons();
    }

    public Person getPersonByFirstnameAndLastname(String firstName, String lastName) {
        return personRepository.getPersonByFirstnameAndLastname(firstName, lastName);
    }
}
