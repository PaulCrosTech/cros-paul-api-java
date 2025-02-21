package com.openclassrooms.SafetyNet.service;


import com.openclassrooms.SafetyNet.exceptions.ConflictException;
import com.openclassrooms.SafetyNet.exceptions.JsonFileManagerSaveException;
import com.openclassrooms.SafetyNet.exceptions.NotFoundException;
import com.openclassrooms.SafetyNet.model.Person;
import com.openclassrooms.SafetyNet.repository.PersonRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import java.util.Arrays;
import java.util.List;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

/**
 * Tests for PersonService
 */
@ExtendWith(MockitoExtension.class)
public class PersonServiceTest {

    private static PersonService personService;

    @Mock
    private PersonRepository personRepository;

    private List<Person> persons;

    /**
     * Set up before each test
     */
    @BeforeEach
    public void setUpPerTest() {
        personService = new PersonService(personRepository);
        persons = Arrays.asList(
                new Person("John", "Boyd", "1509 Culver St", "Culver", "97451", "841-874-6512", "jaboyd@email.com"),
                new Person("Jacob", "Boyd", "1509 Culver St", "Culver", "97451", "841-874-6513", "drk@email.com"),
                new Person("Tenley", "Boyd", "1509 Culver St", "Culver", "97451", "841-874-6512", "tenz@email.com")
        );
    }

    /**
     * Testing method getPersons
     * - Given persons list
     * - Then persons list
     */
    @Test
    public void givenPersonList_whenGetPersons_thenReturnPersonList() {

        // Given
        when(personRepository.getPersons()).thenReturn(persons);

        // When
        List<Person> personList = personService.getPersons();

        // Then
        assertEquals(persons, personList);
    }

    /**
     * Testing method getPersonByFirstNameAndLastName() method from PersonService
     * - Given existing person
     * - Then person
     */
    @Test
    public void givenExistingPerson_whenGetPersonByFirstNameAndLastName_thenReturnPerson() {
        // Given
        Person personExpected = persons.getFirst();

        when(personRepository.getPersonByFirstNameAndLastName(personExpected.getFirstName(), personExpected.getLastName()))
                .thenReturn(personExpected);

        // When
        Person person = personService.getPersonByFirstNameAndLastName(personExpected.getFirstName(), personExpected.getLastName());

        // Then
        assertEquals(personExpected, person);

    }

    /**
     * Testing method getPersonByFirstNameAndLastName
     * - Given non-existing person
     * - Then throw NotFoundException
     */
    @Test
    public void givenNonExistingPerson_whenGetPersonByFirstNameAndLastName_thenThrowNotFoundException() {
        // Given
        String firstName = "UnknownFirstName";
        String lastName = "UnknownLastName";

        when(personRepository.getPersonByFirstNameAndLastName(firstName, lastName)).thenReturn(null);

        // When && Then
        assertThrows(NotFoundException.class,
                () -> personService.getPersonByFirstNameAndLastName(firstName, lastName)
        );
    }

    /**
     * Testing method deletePersonByFirstNameAndLastName
     * - Given existing person
     * - Then person is deleted
     *
     * @throws Exception Exception
     */
    @Test
    public void givenExistingPerson_whenDeletePersonByFirstNameAndLastName_thenPersonDeleted() throws Exception {
        // Given
        Person personExpected = persons.getFirst();

        when(personRepository.deletePersonByFirstNameAndLastName(personExpected.getFirstName(), personExpected.getLastName()))
                .thenReturn(true);

        // When
        personService.deletePersonByFirstNameAndLastName(personExpected.getFirstName(), personExpected.getLastName());

        // Then
        verify(personRepository, times(1))
                .deletePersonByFirstNameAndLastName(personExpected.getFirstName(), personExpected.getLastName());
    }

    /**
     * Testing method deletePersonByFirstNameAndLastName
     * - Given non-existing person
     * - Then NotFoundException
     */
    @Test
    public void givenNonExistingPerson_whenDeletePersonByFirstNameAndLastName_thenThrowNotFoundException() {
        // Given
        String firstName = "UnknownFirstName";
        String lastName = "UnknownLastName";

        when(personRepository.deletePersonByFirstNameAndLastName(firstName, lastName)).thenReturn(false);

        // When && Then
        assertThrows(NotFoundException.class,
                () -> personService.deletePersonByFirstNameAndLastName(firstName, lastName)
        );
    }

    /**
     * Testing method deletePersonByFirstNameAndLastName
     * - Given existing person
     * - Then throw JsonFileManagerSaveException
     */
    @Test
    public void givenExistingPerson_whenDeletePersonByFirstNameAndLastName_thenThrowJsonFileManagerSaveException() {
        // Given
        Person personExpected = persons.getFirst();

        doThrow(
                new JsonFileManagerSaveException("Error while deleting the person in JSON file, firstName: " + personExpected.getFirstName() + " and lastName: " + personExpected.getLastName())
        )
                .when(personRepository)
                .deletePersonByFirstNameAndLastName(personExpected.getFirstName(), personExpected.getLastName());

        // When && Then
        assertThrows(Exception.class,
                () -> personService.deletePersonByFirstNameAndLastName(personExpected.getFirstName(), personExpected.getLastName())
        );
    }

    /**
     * Testing method savePerson
     * - Given new person
     * - Then person saved
     */
    @Test
    public void givenNewPerson_whenSavePerson_thenPersonSaved() {
        // Given
        Person personExpected = new Person("NewFirstName", "NewLastName", "New Address", "New City", "99999", "999-999-9999", "newmail@mail.com");

        when(personRepository.getPersonByFirstNameAndLastName(personExpected.getFirstName(), personExpected.getLastName()))
                .thenReturn(null);

        // When
        personService.savePerson(personExpected);

        // Then
        verify(personRepository, times(1)).savePerson(personExpected);
    }

    /**
     * Testing method savePerson() method from PersonService
     * - Given existing person
     * - Then throw ConflictException
     */
    @Test
    public void givenExistingPerson_whenSavePerson_thenThrowConflictException() {
        // Given
        Person personExpected = persons.getFirst();

        when(personRepository.getPersonByFirstNameAndLastName(personExpected.getFirstName(), personExpected.getLastName()))
                .thenReturn(personExpected);

        // When && Then
        assertThrows(ConflictException.class,
                () -> personService.savePerson(personExpected)
        );
    }


    /**
     * Testing method savePerson
     * - Given new person
     * - Then JsonFileManagerSaveException
     */
    @Test
    public void givenNewPerson_whenSavePerson_thenThrowJsonFileManagerSaveException() {
        // Given
        Person personExpected = new Person("NewFirstName", "NewLastName", "New Address", "New City", "99999", "999-999-9999", "newmail@mail.com");

        when(personRepository.getPersonByFirstNameAndLastName(personExpected.getFirstName(), personExpected.getLastName()))
                .thenReturn(null);
        doThrow(new JsonFileManagerSaveException("Error while saving the person in JSON file"))
                .when(personRepository).savePerson(personExpected);

        // When && Then
        assertThrows(JsonFileManagerSaveException.class,
                () -> personService.savePerson(personExpected)
        );

    }

    /**
     * Testing updatePerson() method from PersonService
     */
    @Test
    public void givenExistingPerson_whenUpdatePerson_thenReturnPersonUpdated() {
        // Given
        Person personExpected = persons.getFirst();
        personExpected.setAddress("Address Updated");

        when(personRepository.updatePerson(personExpected)).thenReturn(personExpected);

        // When
        Person person = personService.updatePerson(personExpected);

        // Then
        assertEquals(personExpected, person);
    }

    /**
     * Testing updatePerson() method from PersonService
     */
    @Test
    public void givenNonExistingPerson_whenUpdatePerson_thenThrowNotFoundException() {
        // Given
        Person personExpected = new Person("UnknowFirstName", "UnknowLastName", "New Address", "New City", "99999", "999-999-9999", "newmail@mail.com");

        when(personRepository.updatePerson(personExpected)).thenReturn(null);

        // When && Then
        assertThrows(NotFoundException.class,
                () -> personService.updatePerson(personExpected)
        );
    }

}
