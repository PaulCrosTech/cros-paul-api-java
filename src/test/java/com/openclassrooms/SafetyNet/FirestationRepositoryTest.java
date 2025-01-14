package com.openclassrooms.SafetyNet;


import com.openclassrooms.SafetyNet.model.Firestation;
import com.openclassrooms.SafetyNet.model.Person;
import com.openclassrooms.SafetyNet.repository.FirestationRepository;
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
public class FirestationRepositoryTest {

    private static FirestationRepository firestationRepository;

    @Mock
    private JsonFileManager jsonFileManager;

    private List<Firestation> firestations;

    @BeforeEach
    public void setUp() {
        firestationRepository = new FirestationRepository(jsonFileManager);
        firestations = new ArrayList<>(Arrays.asList(
                new Firestation("1509 Culver St", 3),
                new Firestation("29 15th St", 2),
                new Firestation("834 Binoc Ave", 3)
        ));
    }


    /**
     * Testing getFirestations() method from FirestationRepository
     */
    @Test
    public void givenNothing_whenGetFirestations_thenReturnFirestationList() {

        // Given
        when(jsonFileManager.getFirestations()).thenReturn(firestations);

        // When
        List<Firestation> firestationList = firestationRepository.getFirestations();

        // Then
        assertEquals(firestations, firestationList);
    }

    /**
     * Testing getFirestationByStationNumber() method from FirestationRepository
     */
    @Test
    public void givenExistingStationNumber_whenGetFirestationsByStationNumber_thenReturnFirestationList() {

        // Given
        when(jsonFileManager.getFirestations()).thenReturn(firestations);

        // When
        List<Firestation> firestationList = firestationRepository.getFirestationByStationNumber(3);

        // Then
        assertEquals(2, firestationList.size());
    }

    /**
     * Testing getFirestationByAddress() method from FirestationRepository
     */
    @Test
    public void givenExistingAddress_whenGetFirestationByAddress_thenReturnFirestation() {
        // Given
        Firestation firestationExpected = firestations.getFirst();
        when(jsonFileManager.getFirestations()).thenReturn(firestations);

        // When
        Firestation firestation = firestationRepository.getFirestationByAddress(firestationExpected.getAddress());

        // Then
        assertEquals(firestationExpected, firestation);
    }

    /**
     * Testing getFirestationByAddress() method from FirestationRepository
     */
    @Test
    public void givenNonExistingAddress_whenGetFirestationByAddress_thenReturnNull() {
        // Given
        when(jsonFileManager.getFirestations()).thenReturn(firestations);

        // When
        Firestation firestation = firestationRepository.getFirestationByAddress("UnknownAddress");

        // Then
        assertNull(firestation);
    }

    /**
     * Testing deleteFirestationByAddress() method from FirestationRepository
     */
    @Test
    public void givenExistinsAddress_whenDeleteFirestationByAddress_thenReturnTrue() {
        // Given
        Firestation firestationExpected = firestations.getFirst();
        when(jsonFileManager.getFirestations()).thenReturn(firestations);

        // When
        boolean isDeleted = firestationRepository.deleteFirestationByAddress(firestationExpected.getAddress());

        // Then
        assertTrue(isDeleted);
        verify(jsonFileManager, times(1)).saveJsonFile();
        assertFalse(firestations.contains(firestationExpected));
    }

    /**
     * Testing deleteFirestationByAddress() method from FirestationRepository
     */
    @Test
    public void givenNonExistingAddress_whenDeleteFirestationByAddress_thenReturnFalse() {
        // Given
        when(jsonFileManager.getFirestations()).thenReturn(firestations);

        // When
        boolean isDeleted = firestationRepository.deleteFirestationByAddress("UnknownAddress");

        // Then
        assertFalse(isDeleted);
        verify(jsonFileManager, times(0)).saveJsonFile();
    }

    /**
     * Testing saveFirestation() method from FirestationRepository
     */
    @Test
    public void givenNewFirestation_whenSaveFirestation_thenFirestationSaved() {
        // Given
        Firestation firestation = new Firestation("NewAddress", 99);
        when(jsonFileManager.getFirestations()).thenReturn(firestations);

        // When
        firestationRepository.saveFirestation(firestation);

        // Then
        verify(jsonFileManager, times(1)).saveJsonFile();
        assertTrue(firestations.contains(firestation));
    }

    /**
     * Testing updateFirestation() method from FirestationRepository
     */
    @Test
    public void givenExistingFirestation_whenUpdateFirestation_thenFirestationUpdated() {
        // Given
        Firestation firestationExpected = firestations.getFirst();
        firestationExpected.setStation(99);
        when(jsonFileManager.getFirestations()).thenReturn(firestations);

        // When
        Firestation firestation = firestationRepository.updateFirestation(firestationExpected);

        // Then
        verify(jsonFileManager, times(1)).saveJsonFile();
        assertEquals(firestationExpected, firestation);
    }

    /**
     * Testing updateFirestation() method from FirestationRepository
     */
    @Test
    public void givenNonExistingFirestation_whenUpdateFirestation_thenReturnNull() {
        // Given
        Firestation firestationToUpdate = new Firestation("UnknownAddress", 99);
        when(jsonFileManager.getFirestations()).thenReturn(firestations);

        // When
        Firestation firestation = firestationRepository.updateFirestation(firestationToUpdate);

        // Then
        verify(jsonFileManager, times(0)).saveJsonFile();
        assertNull(firestation);
    }

}
