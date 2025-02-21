package com.openclassrooms.SafetyNet.repository;

import com.openclassrooms.SafetyNet.exceptions.JsonFileManagerSaveException;
import com.openclassrooms.SafetyNet.model.MedicalRecord;
import com.openclassrooms.SafetyNet.utils.JsonFileManager;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * MedicalRecordRepository Class
 */
@Log4j2
@Repository
public class MedicalRecordRepository {

    private final JsonFileManager jsonFileManager;

    /**
     * Constructor
     *
     * @param jsonFileManager JsonFileManager
     */
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
        MedicalRecord medicalRecord = getMedicalRecords().stream()
                .filter(m -> m.getFirstName().equals(firstName) && m.getLastName().equals(lastName))
                .findFirst()
                .orElse(null);
        log.debug("Medical record for {} {} {}", firstName, lastName, medicalRecord != null ? "found" : "not found");
        return medicalRecord;
    }

    /**
     * Get the birthdate by first name and last name
     *
     * @param firstName String
     * @param lastName  String
     * @return String birthdate
     */
    public String getBirthdateByFirstNameAndLastName(String firstName, String lastName) {
        MedicalRecord medicalRecord = getMedicalRecordByFirstNameAndLastName(firstName, lastName);
        log.debug("Medical record for {} {} {}", firstName, lastName, medicalRecord != null ? "found" : "not found");
        return (medicalRecord == null) ? null : medicalRecord.getBirthdate();
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
        List<MedicalRecord> medicalRecords = getMedicalRecords();
        medicalRecords.add(medicalRecord);
        log.debug("Medical record {} {} saved", medicalRecord.getFirstName(), medicalRecord.getLastName());
        jsonFileManager.saveJsonFile();
    }


    /**
     * Update a medical record
     *
     * @param medicalRecord MedicalRecord object with the new information
     * @return MedicalRecord object updated
     * @throws JsonFileManagerSaveException if an error occurs while saving the file
     */
    public MedicalRecord updateMedicalRecord(MedicalRecord medicalRecord) throws JsonFileManagerSaveException {
        MedicalRecord existingRecord = getMedicalRecordByFirstNameAndLastName(medicalRecord.getFirstName(), medicalRecord.getLastName());
        if (existingRecord != null) {
            existingRecord.setBirthdate(medicalRecord.getBirthdate());
            existingRecord.setMedications(medicalRecord.getMedications());
            existingRecord.setAllergies(medicalRecord.getAllergies());
            jsonFileManager.saveJsonFile();
            return existingRecord;
        }
        log.debug("Medical record {} {} not found", medicalRecord.getFirstName(), medicalRecord.getLastName());
        return null;
    }

}
