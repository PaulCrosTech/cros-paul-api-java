package com.openclassrooms.SafetyNet;


import com.openclassrooms.SafetyNet.model.Person;
import com.openclassrooms.SafetyNet.repository.JsonFileManager;
import com.openclassrooms.SafetyNet.repository.PersonRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PersonRepositoryTest {

    private static PersonRepository personRepository;

    @Mock
    private JsonFileManager jsonFileManager;

    private List<Person> persons;

    @BeforeEach
    public void setUp() {
        personRepository = new PersonRepository(jsonFileManager);
        persons = new ArrayList<>(Arrays.asList(
                new Person("John", "Boyd", "1509 Culver St", "Culver", "97451", "841-874-6512", "jaboyd@email.com"),
                new Person("Jacob", "Boyd", "1509 Culver St", "Culver", "97451", "841-874-6513", "drk@email.com"),
                new Person("Tenley", "Boyd", "1509 Culver St", "Culver", "97451", "841-874-6512", "tenz@email.com")
        ));
    }

    /**
     * Testing getPersons() method from PersonRepository
     */
    @Test
    public void givenNothing_whenGetPersons_thenReturnPersonList() {

        // Given
        when(jsonFileManager.getPersons()).thenReturn(persons);

        // When
        List<Person> personList = personRepository.getPersons();

        // Then
        assertEquals(persons, personList);
    }


    /**
     * Testing getPersonByFirstNameAndLastName() method from PersonRepository
     */
    @Test
    public void givenExistingPerson_whenGetPersonByFirstNameAndLastName_thenReturnPerson() {
        // Given
        Person personExpected = persons.getFirst();
        when(jsonFileManager.getPersons()).thenReturn(persons);

        // When
        Person person = personRepository.getPersonByFirstNameAndLastName(personExpected.getFirstName(), personExpected.getLastName());

        // Then
        assertEquals(personExpected, person);

    }

    /**
     * Testing getPersonByFirstNameAndLastName() method from PersonRepository
     */
    @Test
    public void givenNonExistingPerson_whenGetPersonByFirstNameAndLastName_thenThrowNotFoundException() {
        // Given
        when(jsonFileManager.getPersons()).thenReturn(persons);

        // When
        Person person = personRepository.getPersonByFirstNameAndLastName("UnknowLastName", "UnknowFirstName");

        // Then
        assertNull(person);
    }

    /**
     * Testing getPersonByLastName() method from PersonRepository
     */
    @Test
    public void givenExistingLastName_whenGetPersonByLastName_thenReturnPersonList() {
        // Given
        String lastName = "Boyd";
        when(jsonFileManager.getPersons()).thenReturn(persons);

        // When
        List<Person> personList = personRepository.getPersonByLastName(lastName);

        // Then
        assertEquals(persons, personList);
    }

    /**
     * Testing getPersonByAddress() method from PersonRepository
     */
    @Test
    public void givenExistingAddress_whenGetPersonByAddress_thenReturnPersonList() {
        // Given
        String address = "1509 Culver St";
        when(jsonFileManager.getPersons()).thenReturn(persons);

        // When
        List<Person> personList = personRepository.getPersonByAddress(address);

        // Then
        assertEquals(persons, personList);
    }

    /**
     * Testing addPerson() method from PersonRepository
     */
    @Test
    public void givenExistingPerson_whenDeletePersonByFirstNameAndLastName_thenPersonDeleted() {
        // Given
        Person personToDelete = persons.getFirst();
        when(jsonFileManager.getPersons()).thenReturn(persons);

        // When
        personRepository.deletePersonByFirstNameAndLastName(personToDelete.getFirstName(), personToDelete.getLastName());

        // Then
        verify(jsonFileManager, times(1)).saveJsonFile();
        assertEquals(2, personRepository.getPersons().size());
        assertFalse(persons.contains(personToDelete));
    }

    /**
     * Testing addPerson() method from PersonRepository
     */
    @Test
    public void givenNonExistingPerson_whenDeletePersonByFirstNameAndLastName_thenThrowNotFoundException() {
        // Given
        when(jsonFileManager.getPersons()).thenReturn(persons);

        // When
        boolean deleted = personRepository.deletePersonByFirstNameAndLastName("UnknowLastName", "UnknowFirstName");

        // Then
        assertFalse(deleted);
        verify(jsonFileManager, times(0)).saveJsonFile();
    }

    /**
     * Testing addPerson() method from PersonRepository
     */
    @Test
    public void givenNewPerson_whenSavePerson_thenPersonSaved() {
        // Given
        Person personToSave = new Person("NewFirstName", "NewLastName", "New Address", "New City", "99999", "999-999-9999", "bnewemail@mail.com");

        when(jsonFileManager.getPersons()).thenReturn(persons);

        // When
        personRepository.savePerson(personToSave);

        // Then
        verify(jsonFileManager, times(1)).saveJsonFile();
        assertTrue(persons.contains(personToSave));

    }

    /**
     * Testing addPerson() method from PersonRepository
     */
    @Test
    public void givenExistingPerson_whenUpdatePerson_thenReturnPersonUpdated() {
        // Given
        Person personToUpdate = persons.getFirst();
        personToUpdate.setAddress("NewAddress");

        when(jsonFileManager.getPersons()).thenReturn(persons);

        // When
        Person person = personRepository.updatePerson(personToUpdate);

        // Then
        verify(jsonFileManager, times(1)).saveJsonFile();
        assertEquals("NewAddress", person.getAddress());
    }

    /**
     * Testing addPerson() method from PersonRepository
     */
    @Test
    public void givenNonExistingPerson_whenUpdatePerson_thenReturnNull() {
        // Given
        Person personToUpdate = new Person("UnknowFirstName", "UnknowLastName", "New Address", "New City", "99999", "999-999-9999", "mail@mail.com");

        when(jsonFileManager.getPersons()).thenReturn(persons);

        // When
        Person person = personRepository.updatePerson(personToUpdate);

        // Then
        assertNull(person);

    }

}
