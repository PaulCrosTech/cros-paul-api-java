package com.openclassrooms.SafetyNet.service;

import com.openclassrooms.SafetyNet.exceptions.*;
import com.openclassrooms.SafetyNet.model.Firestation;
import com.openclassrooms.SafetyNet.repository.FirestationRepository;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * FirestationService Class
 */
@Log4j2
@Service
@Data
public class FirestationService {

    private final FirestationRepository firestationRepository;

    /**
     * Constructor
     *
     * @param firestationRepository firestation repository
     */
    public FirestationService(FirestationRepository firestationRepository) {
        log.info("<constructor> FirestationService");
        this.firestationRepository = firestationRepository;
    }

    /**
     * Get all fire stations
     *
     * @return List of Firestation objects
     */
    public List<Firestation> getFirestations() {
        List<Firestation> firestations = firestationRepository.getFirestations();
        log.info("{} fire stations found", firestations.size());
        return firestations;
    }


    /**
     * Get fire station by address
     *
     * @param address String address of the fire station (case-sensitive)
     * @return Fire station object
     * @throws NotFoundException if fire station not found
     */
    public Firestation getFirestationByAddress(String address) throws NotFoundException {
        Firestation firestation = firestationRepository.getFirestationByAddress(address);
        if (firestation == null) {
            throw new NotFoundException("Fire station with address " + address + " not found");
        }
        log.info("Fire station with address {} found", address);
        return firestation;
    }


    /**
     * Delete a fire station by address
     *
     * @param address String address of the fire station (case-sensitive)
     * @throws Exception if error while deleting
     */
    public void deleteFirestationByAddress(String address) throws Exception {
        try {
            boolean deleted = firestationRepository.deleteFirestationByAddress(address);
            if (!deleted) {
                throw new NotFoundException("Fire station not found with address : " + address);
            }
        } catch (JsonFileManagerSaveException e) {
            throw new Exception("Error while deleting the fire station in JSON file, address: " + address);
        }
        log.info("Fire station wtih address {} deleted", address);
    }


    /**
     * Save a fire station
     *
     * @param firestation Firestation object to save
     * @throws JsonFileManagerSaveException if error while saving
     * @throws ConflictException            if fire station already exist
     */
    public void saveFirestation(Firestation firestation) throws JsonFileManagerSaveException, ConflictException {

        // Vérifie si Firestation existe déjà
        Firestation firestationExist = firestationRepository.getFirestationByAddress(firestation.getAddress());
        if (firestationExist != null) {
            throw new ConflictException("Fire station already exist with address: " + firestation.getAddress());

        }
        // Création de Firestation
        try {
            firestationRepository.saveFirestation(firestation);
        } catch (JsonFileManagerSaveException ex) {
            throw new JsonFileManagerSaveException("Error while saving the fire station in JSON file");
        }
        log.info("Firestation number {} at {} saved", firestation.getStation(), firestation.getAddress());

    }

    /**
     * Update station number of all fire station matching the address
     *
     * @param firestation Firestation object to update
     * @return List of Firestation objects updated
     * @throws NotFoundException if fire station not found
     */
    public Firestation updateFirestation(Firestation firestation) throws NotFoundException {

        Firestation firestationUpdated = firestationRepository.updateFirestation(firestation);
        if (firestationUpdated == null) {
            throw new NotFoundException("Fire station not found with address: " + firestation.getAddress());
        }

        log.debug("Fire station with address {} updated", firestationUpdated.getAddress());
        return firestationUpdated;
    }
}
