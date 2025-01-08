package com.openclassrooms.SafetyNet.repository;

import com.openclassrooms.SafetyNet.exceptions.JsonFileManagerSaveException;
import com.openclassrooms.SafetyNet.model.MedicalRecord;
import com.openclassrooms.SafetyNet.dto.MedicalRecordUpdateDTO;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Log4j2
@Repository
public class MedicalRecordRepository {

    private final JsonFileManager jsonFileManager;

    @Autowired
    public MedicalRecordRepository(JsonFileManager jsonFileManager) {
        log.info("<constructor> MedicalRecordRepository");
        this.jsonFileManager = jsonFileManager;
    }

    /**
     * Get all Medical Records
     *
     * @return List of Medical records objects
     */
    public List<MedicalRecord> getMedicalRecords() {
        return jsonFileManager.getMedicalRecords();
    }

    /**
     * Get a Medical record by first name and last name
     *
     * @param firstName String
     * @param lastName  String
     * @return Medical record object
     */
    public MedicalRecord getMedicalRecordByFirstNameAndLastName(String firstName, String lastName) {
        for (MedicalRecord medicalRecord : getMedicalRecords()) {
            if (medicalRecord.getFirstName().equals(firstName) && medicalRecord.getLastName().equals(lastName)) {
                log.debug("Medical record {} {} found", firstName, lastName);
                return medicalRecord;
            }
        }
        log.debug("Medical record {} {} not found", firstName, lastName);
        return null;
    }

    /**
     * Delete a medical record by first name and last name
     *
     * @param firstName String case-sensitive
     * @param lastName  String case-sensitive
     * @return boolean if a medical record is deleted
     * @throws JsonFileManagerSaveException if an error occurs while saving the file
     */
    public boolean deleteMedicalRecordByFirstNameAndLastName(String firstName, String lastName) throws JsonFileManagerSaveException {
        // Ici, on récupère la référence et non une copie de la liste
        List<MedicalRecord> medicalRecords = getMedicalRecords();
        boolean deleted = medicalRecords.removeIf(medicalRecord ->
                medicalRecord.getFirstName().equals(firstName) && medicalRecord.getLastName().equals(lastName));
        if (deleted) {
            jsonFileManager.saveJsonFile();
        }
        log.debug("Medical record {} {} deleted : {} ", firstName, lastName, deleted);
        return deleted;
    }

    /**
     * Save a medical record
     *
     * @param medicalRecord MedicalRecord object
     * @throws JsonFileManagerSaveException if an error occurs while saving the file
     */
    public void saveMedicalRecord(MedicalRecord medicalRecord) throws JsonFileManagerSaveException {
        // Ici, on récupère la référence et non une copie de la liste
        List<MedicalRecord> medicalRecords = getMedicalRecords();
        medicalRecords.add(medicalRecord);
        log.debug("Medical record {} {} saved", medicalRecord.getFirstName(), medicalRecord.getLastName());
        jsonFileManager.saveJsonFile();
    }


    /**
     * Update a medical record
     *
     * @param firstName     first name of the person to update
     * @param lastName      last name of the person to update
     * @param medicalRecord MedicalRecordUpdateDTO object with the new information
     * @return MedicalRecord object updated
     * @throws JsonFileManagerSaveException if an error occurs while saving the file
     */
    public MedicalRecord updateMedicalRecord(String firstName, String lastName, MedicalRecordUpdateDTO medicalRecord) throws JsonFileManagerSaveException {
        // Ici, on récupère la référence et non une copie de la liste
        MedicalRecord existingRecord = getMedicalRecordByFirstNameAndLastName(firstName, lastName);
        if (existingRecord != null) {
            existingRecord.setBirthdate(medicalRecord.getBirthdate());
            existingRecord.setMedications(medicalRecord.getMedications());
            existingRecord.setAllergies(medicalRecord.getAllergies());
            jsonFileManager.saveJsonFile();
            return existingRecord;
        }
        log.debug("Medical record {} {} not found", firstName, lastName);
        return null;
    }

}
