package com.openclassrooms.SafetyNet.repository;

import com.openclassrooms.SafetyNet.exceptions.JsonFileManagerSaveException;
import com.openclassrooms.SafetyNet.model.Firestation;
import com.openclassrooms.SafetyNet.model.FirestationUpdateDTO;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Log4j2
@Repository
public class FirestationRepository {

    private final JsonFileManager jsonFileManager;

    @Autowired
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
        log.info("<repo> getFirestations");
        return jsonFileManager.getFirestations();
    }

    /**
     * Get all fire stations by station number
     *
     * @param station String number of the station
     * @return List of Firestation objects
     */
    public List<Firestation> getFirestationByStation(String station) {
        log.info("<repo> getFirestationByStation : station: {}", station);
        List<Firestation> f = new ArrayList<>();
        for (Firestation firestation : getFirestations()) {
            if (firestation.getStation().equals(station)) {
                log.info("<repo> getFirestationByStation : firestation found");
                f.add(firestation);
            }
        }
        return f;
    }

    /**
     * Get a fire station by address
     *
     * @param address String address of the fire station (case-sensitive)
     * @return Firestation object
     */
    public Firestation getFirestationByAddress(String address) {
        log.info("<repo> getFirestationByAddress : address: {}", address);
        for (Firestation firestation : getFirestations()) {
            if (firestation.getAddress().equals(address)) {
                log.info("<repo> getFirestationByAddress : firestation found");
                return firestation;
            }
        }
        return null;
    }

    /**
     * Delete a fire station by address
     *
     * @param address String address of the fire station (case-sensitive)
     * @throws JsonFileManagerSaveException if an error occurs while saving the file
     */
    public boolean deleteFirestationByAddress(String address) throws JsonFileManagerSaveException {
        log.info("<repo> deleteFirestationByAddress : address: {}", address);
        // Ici, on récupère la référence et non une copie de la liste
        List<Firestation> firestations = getFirestations();
        boolean deleted = firestations.removeIf(firestation -> firestation.getAddress().equals(address));
        if (deleted) {
            log.info("<repo> deleteFirestationByAddress : firestation deleted");
            jsonFileManager.saveJsonFile();
        }
        return deleted;
    }


    /**
     * Save a fire station
     *
     * @param firestation Firestation object to save
     * @throws JsonFileManagerSaveException if an error occurs while saving the file
     */
    public void saveFirestation(Firestation firestation) throws JsonFileManagerSaveException {
        log.info("<repo> saveFirestation : firestation: {}", firestation);
        // Ici, on récupère la référence et non une copie de la liste
        List<Firestation> firestations = getFirestations();
        firestations.add(firestation);
        jsonFileManager.saveJsonFile();
    }


    /**
     * Update a fire station
     *
     * @param address     address of the fire station to update
     * @param firestation Firestation object to update
     * @return Firestation object updated
     * @throws JsonFileManagerSaveException if an error occurs while saving the file
     */
    public Firestation updateFirestation(String address, FirestationUpdateDTO firestation) throws JsonFileManagerSaveException {
        log.info("<repo> updateFirestation : firestation: {}", firestation);
        // Ici, on récupère la référence et non une copie de la liste
        Firestation existingFirestation = getFirestationByAddress(address);
        if (existingFirestation != null) {
            existingFirestation.setStation(firestation.getStation());
            jsonFileManager.saveJsonFile();
            return existingFirestation;
        }
        return null;
    }

}
