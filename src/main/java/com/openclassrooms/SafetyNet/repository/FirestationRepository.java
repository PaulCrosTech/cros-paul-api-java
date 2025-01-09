package com.openclassrooms.SafetyNet.repository;

import com.openclassrooms.SafetyNet.exceptions.JsonFileManagerSaveException;
import com.openclassrooms.SafetyNet.model.Firestation;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

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
     * Get all fire stations by station number
     *
     * @param station String number of the station
     * @return List of Firestation objects
     */
    public List<Firestation> getFirestationByStationNumber(String station) {
        List<Firestation> f = new ArrayList<>();
        for (Firestation firestation : getFirestations()) {
            if (firestation.getStation().equals(station)) {
                f.add(firestation);
            }
        }
        log.debug("Firestation {} found", station);
        return f;
    }

    /**
     * Get a fire station by address
     *
     * @param address String address of the fire station (case-sensitive)
     * @return Firestation object
     */
    // TODO : à revoir, car une adresse peut être couverte par plusieurs casernes (voir l'impact sur le reste du code)
    public Firestation getFirestationByAddress(String address) {
        for (Firestation firestation : getFirestations()) {
            if (firestation.getAddress().equals(address)) {
                log.debug("Firestation {} found", address);
                return firestation;
            }
        }
        log.debug("Firestation {} not found", address);
        return null;
    }

    /**
     * Delete a fire station by address
     *
     * @param address String address of the fire station (case-sensitive)
     * @throws JsonFileManagerSaveException if an error occurs while saving the file
     */
    // TODO : à revoir, car une adresse peut être couverte par plusieurs casernes
    public boolean deleteFirestationByAddress(String address) throws JsonFileManagerSaveException {
        // Ici, on récupère la référence et non une copie de la liste
        List<Firestation> firestations = getFirestations();
        boolean deleted = firestations.removeIf(firestation -> firestation.getAddress().equals(address));
        if (deleted) {
            jsonFileManager.saveJsonFile();
        }
        log.debug("Firestation {} deleted : {} ", address, deleted);
        return deleted;
    }


    /**
     * Save a fire station
     *
     * @param firestation Firestation object to save
     * @throws JsonFileManagerSaveException if an error occurs while saving the file
     */
    public void saveFirestation(Firestation firestation) throws JsonFileManagerSaveException {
        // Ici, on récupère la référence et non une copie de la liste
        List<Firestation> firestations = getFirestations();
        firestations.add(firestation);
        log.debug("Firestation {} saved", firestation);
        jsonFileManager.saveJsonFile();
    }


    /**
     * Update a fire station
     *
     * @param firestation Firestation object to update
     * @return Firestation object updated
     * @throws JsonFileManagerSaveException if an error occurs while saving the file
     */
    public Firestation updateFirestation(Firestation firestation) throws JsonFileManagerSaveException {
        // Ici, on récupère la référence et non une copie de la liste
        Firestation existingFirestation = getFirestationByAddress(firestation.getAddress());
        if (existingFirestation != null) {
            existingFirestation.setStation(firestation.getStation());
            jsonFileManager.saveJsonFile();
            return existingFirestation;
        }
        log.debug("Firestation {} not found", firestation.getAddress());
        return null;
    }

}
