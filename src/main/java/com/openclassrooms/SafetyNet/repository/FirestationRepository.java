package com.openclassrooms.SafetyNet.repository;

import com.openclassrooms.SafetyNet.exceptions.JsonFileManagerSaveException;
import com.openclassrooms.SafetyNet.model.Firestation;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * FirestationRepository Class
 */
@Log4j2
@Repository
public class FirestationRepository {

    private final JsonFileManager jsonFileManager;


    /**
     * Constructor
     *
     * @param jsonFileManager JsonFileManager
     */
    public FirestationRepository(JsonFileManager jsonFileManager) {
        log.info("<constructor> PersonRepository");
        this.jsonFileManager = jsonFileManager;
    }

    /**
     * Get all fire stations
     *
     * @return List of Firestation objects
     */
    public List<Firestation> getFirestations() {
        return jsonFileManager.getFirestations();
    }

    /**
     * Get all fire station by station number
     *
     * @param station Integer number of the station
     * @return List of Firestation objects
     */
    public List<Firestation> getFirestationByStationNumber(Integer station) {

        List<Firestation> firestations = getFirestations().stream()
                .filter(f -> f.getStation().equals(station))
                .toList();
        log.debug("{} firestation number {} found", firestations.size(), station);
        return firestations;
    }


    /**
     * Get all fire stations by address
     *
     * @param address String address of the fire station (case-sensitive)
     * @return Firestation object
     */
    public Firestation getFirestationByAddress(String address) {
        Firestation firestation = getFirestations().stream()
                .filter(f -> f.getAddress().equals(address))
                .findFirst()
                .orElse(null);
        log.debug("Firestation wit address {} {}", address, firestation != null ? "found" : "not found");
        return firestation;
    }

    /**
     * Delete a fire station by address
     *
     * @param address String address of the fire station (case-sensitive)
     * @throws JsonFileManagerSaveException if an error occurs while saving the file
     */
    public boolean deleteFirestationByAddress(String address) throws JsonFileManagerSaveException {
        List<Firestation> firestations = getFirestations();
        boolean deleted = firestations.removeIf(firestation -> firestation.getAddress().equals(address));
        if (deleted) {
            jsonFileManager.saveJsonFile();
        }
        log.debug("Firestation wit address {} {} ", address, deleted ? "deleted" : "not found");
        return deleted;
    }


    /**
     * Save a fire station
     *
     * @param firestation Firestation object to save
     * @throws JsonFileManagerSaveException if an error occurs while saving the file
     */
    public void saveFirestation(Firestation firestation) throws JsonFileManagerSaveException {
        List<Firestation> firestations = getFirestations();
        firestations.add(firestation);
        jsonFileManager.saveJsonFile();
        log.debug("Firestation {} saved", firestation);
    }

    /**
     * Update the station number for fire station matching the address
     *
     * @param firestation Firestation object to update
     * @return Firestation object updated
     * @throws JsonFileManagerSaveException if an error occurs while saving the file
     */
    public Firestation updateFirestation(Firestation firestation) throws JsonFileManagerSaveException {
        // Get all firestations matching the address
        Firestation firestationToUpdate = getFirestationByAddress(firestation.getAddress());

        if (firestationToUpdate == null) {
            log.debug("Firestation with address {} not found", firestation.getAddress());
            return null;
        }

        // Update the station number
        firestationToUpdate.setStation(firestation.getStation());
        jsonFileManager.saveJsonFile();

        log.debug("Firestation with address {} updated", firestation.getAddress());
        return firestationToUpdate;

    }

}
