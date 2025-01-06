package com.openclassrooms.SafetyNet.repository;

import com.openclassrooms.SafetyNet.exceptions.JsonFileManagerSaveException;
import com.openclassrooms.SafetyNet.model.MedicalRecord;
import com.openclassrooms.SafetyNet.model.MedicalRecordUpdateDTO;
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
        log.info("<repo> getMedicalRecords");
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
        log.info("<repo> getMedicalRecordByFirstNameAndLastName : firstName: {} and lastName: {}", firstName, lastName);
        for (MedicalRecord medicalRecord : getMedicalRecords()) {
            if (medicalRecord.getFirstName().equals(firstName) && medicalRecord.getLastName().equals(lastName)) {
                log.info("<repo> getMedicalRecordByFirstNameAndLastName : medical record found");
                return medicalRecord;
            }
        }
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
        log.info("<repo> deleteMedicalRecordByFirstNameAndLastName : firstName: {} and lastName: {}", firstName, lastName);
        // Ici, on récupère la référence et non une copie de la liste
        List<MedicalRecord> medicalRecords = getMedicalRecords();
        boolean deleted = medicalRecords.removeIf(medicalRecord ->
                medicalRecord.getFirstName().equals(firstName) && medicalRecord.getLastName().equals(lastName));
        if (deleted) {
            log.info("<repo> deleteMedicalRecordByFirstNameAndLastName : medical record deleted");
            jsonFileManager.saveJsonFile();
        }
        return deleted;
    }

    /**
     * Save a medical record
     *
     * @param medicalRecord MedicalRecord object
     * @throws JsonFileManagerSaveException if an error occurs while saving the file
     */
    public void saveMedicalRecord(MedicalRecord medicalRecord) throws JsonFileManagerSaveException {
        log.info("<repo> saveMedicalRecord : MedicalRecord: {}", medicalRecord);
        // Ici, on récupère la référence et non une copie de la liste
        List<MedicalRecord> medicalRecords = getMedicalRecords();
        medicalRecords.add(medicalRecord);
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
        log.info("<repo> updateMedicalRecord : MedicalRecord: {}", medicalRecord);
        // Ici, on récupère la référence et non une copie de la liste
        // TODO : ici pourquoi ne pas faire un getMedicalRecordByFirstNameAndLastName ? (pour éviter de parcourir la liste)
        MedicalRecord existingRecord = getMedicalRecordByFirstNameAndLastName(firstName, lastName);
        if (existingRecord != null) {
            existingRecord.setBirthdate(medicalRecord.getBirthdate());
            existingRecord.setMedications(medicalRecord.getMedications());
            existingRecord.setAllergies(medicalRecord.getAllergies());
            jsonFileManager.saveJsonFile();
            return existingRecord;
        }
        return null;
//        List<MedicalRecord> medicalRecords = getMedicalRecords();
//        for (MedicalRecord p : persons) {
//            if (p.getFirstName().equals(firstName) && p.getLastName().equals(lastName)) {
//                p.setAddress(person.getAddress());
//                p.setCity(person.getCity());
//                p.setZip(person.getZip());
//                p.setPhone(person.getPhone());
//                p.setEmail(person.getEmail());
//                jsonFileManager.saveJsonFile();
//                return p;
//            }
//        }
//        return null;
    }

}
