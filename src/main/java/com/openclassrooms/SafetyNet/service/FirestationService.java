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
        List<Firestation> firestations = firestationRepository.getFirestations();
        log.info("{} fire stations found", firestations.size());
        return firestations;
    }


    /**
     * Get a fire stations by address
     *
     * @param address String address of the fire station (case-sensitive)
     * @return Firestation objects
     * @throws NotFoundException if fire station not found
     */
    public Firestation getFirestationByAddress(String address) throws NotFoundException {
        Firestation firestation = firestationRepository.getFirestationByAddress(address);
        if (firestation == null) {
            throw new NotFoundException("Fire station not found with address: " + address);
        }
        log.info("Firestation at {} found", address);
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
        log.info("Firestation at {} deleted", address);
    }


    public void saveFirestation(Firestation firestation) throws JsonFileManagerSaveException, ConflictException {
        try {
            // Vérifie si Firestation existe déjà
            Firestation firestationExist = getFirestationByAddress(firestation.getAddress());
            if (firestationExist != null) {
                throw new ConflictException("Fire station already exist with address: " + firestation.getAddress());
            }
        } catch (NotFoundException e) {
            // Création de Firestation
            try {
                firestationRepository.saveFirestation(firestation);
            } catch (JsonFileManagerSaveException ex) {
                throw new JsonFileManagerSaveException("Error while saving the fire station in JSON file");
            }
            log.info("Firestation at {} saved", firestation.getAddress());
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
        Firestation firestationUpdated = firestationRepository.updateFirestation(address, firestation);
        if (firestationUpdated == null) {
            throw new NotFoundException("Fire station not found with address: " + address);
        }
        log.info("Firestation at {} updated", address);

        return firestationUpdated;
    }
}
