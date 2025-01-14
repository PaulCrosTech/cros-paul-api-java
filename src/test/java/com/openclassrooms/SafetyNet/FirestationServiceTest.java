package com.openclassrooms.SafetyNet;


import com.openclassrooms.SafetyNet.exceptions.ConflictException;
import com.openclassrooms.SafetyNet.exceptions.JsonFileManagerSaveException;
import com.openclassrooms.SafetyNet.exceptions.NotFoundException;
import com.openclassrooms.SafetyNet.model.Firestation;
import com.openclassrooms.SafetyNet.repository.FirestationRepository;
import com.openclassrooms.SafetyNet.service.FirestationService;
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

@ExtendWith(MockitoExtension.class)
public class FirestationServiceTest {

    private static FirestationService firestationService;

    @Mock
    private FirestationRepository firestationRepository;

    private List<Firestation> firestations;

    @BeforeEach
    public void setUp() {
        firestationService = new FirestationService(firestationRepository);
        firestations = Arrays.asList(
                new Firestation("1509 Culver St", 3),
                new Firestation("29 15th St", 2),
                new Firestation("834 Binoc Ave", 3)
        );
    }

    /**
     * Testing getFirestations() method from FirestationService
     */
    @Test
    public void givenNothing_whenGetFirestations_thenReturnFirestationList() {

        // Given
        when(firestationRepository.getFirestations()).thenReturn(firestations);

        // When
        List<Firestation> firestationList = firestationService.getFirestations();

        // Then
        assertEquals(firestations, firestationList);
    }


    /**
     * Testing getFirestationByAddress() method from FirestationService
     */
    @Test
    public void givenExtingAddress_whenGetFirestationByAddress_thenReturnFirestation() {
        // Given
        when(firestationRepository.getFirestationByAddress(firestations.getFirst().getAddress()))
                .thenReturn(firestations.getFirst());

        // When
        Firestation firestation = firestationService.getFirestationByAddress(firestations.getFirst().getAddress());

        // Then
        assertEquals(firestations.getFirst(), firestation);
    }

    /**
     * Testing getFirestationByAddress() method from FirestationService
     */
    @Test
    public void givenNonExstinAddress_whenGetFirestationByAddress_thenThrowNotFoundException() {
        // Given
        String address = "UnknownAddress";
        when(firestationRepository.getFirestationByAddress(address)).thenReturn(null);

        // When && Then
        assertThrows(NotFoundException.class, () -> firestationService.getFirestationByAddress(address));
    }

    /**
     * Testing deleteFirestationByAddress() method from FirestationService
     *
     * @throws Exception if error while deleting
     */
    @Test
    public void givenExistingAddress_whenDeleteFirestationByAddress_thenFirestationDeleted() throws Exception {
        // Given
        Firestation firestationExpected = firestations.getFirst();
        when(firestationRepository.deleteFirestationByAddress(firestationExpected.getAddress())).thenReturn(true);

        // When
        firestationService.deleteFirestationByAddress(firestationExpected.getAddress());

        // Then
        verify(firestationRepository, times(1))
                .deleteFirestationByAddress(firestationExpected.getAddress());
    }

    /**
     * Testing deleteFirestationByAddress() method from FirestationService
     */
    @Test
    public void givenNonExistingAddress_whenDeleteFirestationByAddress_thenThrowNotFoundException() {
        // Given
        String address = "UnknownAddress";
        when(firestationRepository.deleteFirestationByAddress(address)).thenReturn(false);

        // When && Then
        assertThrows(NotFoundException.class, () -> firestationService.deleteFirestationByAddress(address));
    }

    /**
     * Testing deleteFirestationByAddress() method from FirestationService
     */
    @Test
    public void givenExistingAddress_whenDeleteFirestationByAddress_thenThrowException() {
        // Given
        Firestation firestationExpected = firestations.getFirst();
        when(firestationRepository.deleteFirestationByAddress(firestationExpected.getAddress()))
                .thenThrow(new JsonFileManagerSaveException("Error while deleting the fire station in JSON file, address: " + firestationExpected.getAddress()));

        // When && Then
        assertThrows(Exception.class, () -> firestationService.deleteFirestationByAddress(firestationExpected.getAddress()));
    }

    /**
     * Testing saveFirestation() method from FirestationService
     */
    @Test
    public void givenNewFirestation_whenSaveFirestation_thenFirestationSaved() {
        // Given
        Firestation firestationExpected = new Firestation("NewAddress", 99);
        when(firestationRepository.getFirestationByAddress(firestationExpected.getAddress()))
                .thenReturn(null);

        // When
        firestationService.saveFirestation(firestationExpected);

        // Then
        verify(firestationRepository, times(1)).saveFirestation(firestationExpected);
    }

    /**
     * Testing saveFirestation() method from FirestationService
     */
    @Test
    public void givenExistingFirestation_whenSaveFirestation_thenThrowConflictException() {
        // Given
        Firestation firestationExpected = firestations.getFirst();

        when(firestationRepository.getFirestationByAddress(firestationExpected.getAddress()))
                .thenReturn(firestationExpected);

        // When && Then
        assertThrows(ConflictException.class, () -> firestationService.saveFirestation(firestationExpected));
    }

    /**
     * Testing saveFirestation() method from FirestationService
     */
    @Test
    public void givenNewFirestation_whenSaveFirestation_thenThrowJsonFileManagerSaveException() {
        // Given
        Firestation firestationExpected = new Firestation("NewAddress", 99);

        when(firestationRepository.getFirestationByAddress(firestationExpected.getAddress()))
                .thenReturn(null);

        doThrow(new JsonFileManagerSaveException("Error while saving the fire station in JSON file"))
                .when(firestationRepository).saveFirestation(firestationExpected);

        // When && Then
        assertThrows(JsonFileManagerSaveException.class, () -> firestationService.saveFirestation(firestationExpected));

    }


    /**
     * Testing updateFirestation() method from FirestationService
     */
    @Test
    public void givenExistingFirestation_whenUpdateFirestation_thenReturnFirestationUpdated() {
        // Given
        Firestation firestationExpected = firestations.getFirst();
        firestationExpected.setStation(99);

        when(firestationRepository.updateFirestation(firestationExpected)).thenReturn(firestationExpected);

        // When
        Firestation firestation = firestationService.updateFirestation(firestationExpected);

        // Then
        assertEquals(firestationExpected, firestation);
    }

    /**
     * Testing updateFirestation() method from FirestationService
     */
    @Test
    public void givenNonExistingFirestation_whenUpdateFirestation_thenThrowNotFoundException() {
        // Given
        Firestation firestationExpected = new Firestation("UnknownAddress", 99);

        when(firestationRepository.updateFirestation(firestationExpected)).thenReturn(null);

        // When && Then
        assertThrows(NotFoundException.class, () -> firestationService.updateFirestation(firestationExpected));

    }

}
