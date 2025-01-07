package com.openclassrooms.SafetyNet.service;

import com.openclassrooms.SafetyNet.exceptions.*;
import com.openclassrooms.SafetyNet.model.Firestation;
import com.openclassrooms.SafetyNet.dto.FirestationUpdateDTO;
import com.openclassrooms.SafetyNet.repository.FirestationRepository;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Log4j2
@Service
@Data
public class FirestationService {

    private final FirestationRepository firestationRepository;

    @Autowired
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
        log.info("<service> getFirestations");
        return firestationRepository.getFirestations();
    }


    /**
     * Get a fire stations by address
     *
     * @param address String address of the fire station (case-sensitive)
     * @return Firestation objects
     * @throws NotFoundException if fire station not found
     */
    public Firestation getFirestationByAddress(String address) throws NotFoundException {
        log.info("<service> getFirestationByAddress : address: {}", address);
        Firestation firestation = firestationRepository.getFirestationByAddress(address);
        if (firestation == null) {
            log.info("<service> Firestation not found");
            throw new NotFoundException("Fire station not found with address: " + address);
        }
        log.info("<service> Firestation found");
        return firestation;
    }


    /**
     * Delete a fire station by address
     *
     * @param address String address of the fire station (case-sensitive)
     * @throws Exception if error while deleting
     */
    public void deleteFirestationByAddress(String address) throws Exception {
        log.info("<service> deleteFirestationByAddress : address: {}", address);
        try {
            boolean deleted = firestationRepository.deleteFirestationByAddress(address);
            if (!deleted) {
                log.info("<service> Firestation not found");
                throw new NotFoundException("Fire station not found with address : " + address);
            }
        } catch (JsonFileManagerSaveException e) {
            log.info("<service> Error while deleting in JSON file");
            throw new Exception("Error while deleting the fire station in JSON file, address: " + address);
        }
        log.info("<service> Firestation deleted");
    }


    public void saveFirestation(Firestation firestation) throws JsonFileManagerSaveException, ConflictException {
        log.info("<service> saveFirestation");

        try {
            // Vérifie si Firestation existe déjà
            Firestation firestationExist = getFirestationByAddress(firestation.getAddress());
            if (firestationExist != null) {
                log.info("<service> Firestation already exist");
                throw new ConflictException("Fire station already exist with address: " + firestation.getAddress());
            }
        } catch (NotFoundException e) {
            // Création de Firestation
            try {
                firestationRepository.saveFirestation(firestation);
            } catch (JsonFileManagerSaveException ex) {
                log.info("<service> Error while saving in JSON file");
                throw new JsonFileManagerSaveException("Error while saving the fire station in JSON file");
            }
            log.info("<service> Firestation saved");
        }
    }

    /**
     * Update a fire station
     *
     * @param address     Firestation address (case-sensitive)
     * @param firestation Firestation object to update
     * @return Firestation object updated
     * @throws NotFoundException if fire station not found
     */
    public Firestation updateFirestation(String address, FirestationUpdateDTO firestation) throws NotFoundException {
        log.info("<service> updateFirestation");

        Firestation firestationUpdated = firestationRepository.updateFirestation(address, firestation);
        if (firestationUpdated == null) {
            log.info("<service> Firestation not found");
            throw new NotFoundException("Fire station not found with address: " + address);
        }

        log.info("<service> Firestation updated");

        return firestationUpdated;
    }
}
