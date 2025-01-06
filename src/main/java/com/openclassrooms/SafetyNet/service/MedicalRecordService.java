package com.openclassrooms.SafetyNet.service;

import com.openclassrooms.SafetyNet.exceptions.ConflictException;
import com.openclassrooms.SafetyNet.exceptions.JsonFileManagerSaveException;
import com.openclassrooms.SafetyNet.exceptions.NotFoundException;
import com.openclassrooms.SafetyNet.model.MedicalRecord;
import com.openclassrooms.SafetyNet.model.MedicalRecordUpdateDTO;
import com.openclassrooms.SafetyNet.repository.MedicalRecordRepository;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Log4j2
@Service
@Data
public class MedicalRecordService {

    private final MedicalRecordRepository medicalRecordRepository;

    @Autowired
    public MedicalRecordService(MedicalRecordRepository medicalRecordRepository) {
        log.info("<constructor> MedicalRecordService");
        this.medicalRecordRepository = medicalRecordRepository;
    }

    /**
     * Get all medical records
     *
     * @return List of MedicalRecord objects
     */
    public List<MedicalRecord> getMedicalRecords() {
        log.info("<service> getMedicalRecords");
        return medicalRecordRepository.getMedicalRecords();
    }

    /**
     * Get a medical record by first name and last name
     *
     * @param firstName String case-sensitive
     * @param lastName  String case-sensitive
     * @return MedicalRecord object
     * @throws NotFoundException if medical record not found
     */
    public MedicalRecord getMedicalRecordByFirstNameAndLastName(String firstName, String lastName) throws NotFoundException {
        log.info("<service> getMedicalRecordByFirstNameAndLastName : firstName: {} and lastName: {}", firstName, lastName);
        MedicalRecord medicalRecord = medicalRecordRepository.getMedicalRecordByFirstNameAndLastName(firstName, lastName);
        if (medicalRecord == null) {
            log.info("<service> Medical record not found");
            throw new NotFoundException("Medical record not found with firstName: " + firstName + " and lastName: " + lastName);
        }
        log.info("<service> Medical record found");
        return medicalRecord;
    }

    /**
     * Delete a medical record by first name and last name
     *
     * @param firstName String case-sensitive
     * @param lastName  String case-sensitive
     * @throws Exception if an error occurs while deleting the person
     */
    public void deleteMedicalRecordByFirstNameAndLastName(String firstName, String lastName) throws Exception {
        log.info("<service> deleteMedicalRecordByFirstNameAndLastName : firstName: {} and lastName: {}", firstName, lastName);
        try {
            boolean deleted = medicalRecordRepository.deleteMedicalRecordByFirstNameAndLastName(firstName, lastName);
            if (!deleted) {
                log.info("<service> Medical record not found");
                throw new NotFoundException("Medical record not found with firstName: " + firstName + " and lastName: " + lastName);
            }
        } catch (JsonFileManagerSaveException e) {
            log.info("<service> Error while deleting in JSON file");
            throw new Exception("Error while deleting the medical record in JSON file, firstName: " + firstName + " and lastName: " + lastName);
        }
        log.info("<service> Medical record deleted");
    }

    /**
     * Save a medical record
     *
     * @param medicalRecord MedicalRecord object to be saved
     * @throws JsonFileManagerSaveException if an error occurs while saving the file
     * @throws ConflictException            if medical record already exist
     */
    public void saveMedicalRecord(MedicalRecord medicalRecord) throws JsonFileManagerSaveException, ConflictException {
        log.info("<service> saveMedicalRecord");

        try {
            // Vérifie si le medical record existe déjà
            MedicalRecord medicalRecordExist = getMedicalRecordByFirstNameAndLastName(medicalRecord.getFirstName(), medicalRecord.getLastName());
            if (medicalRecordExist != null) {
                log.info("<service> Medical record already exist");
                throw new ConflictException("Medical record already exist with firstName: " + medicalRecord.getFirstName() + " and lastName: " + medicalRecord.getLastName());
            }
        } catch (NotFoundException e) {
            // Création du medical record
            try {
                medicalRecordRepository.saveMedicalRecord(medicalRecord);
            } catch (JsonFileManagerSaveException ex) {
                log.info("<service> Error while saving in JSON file");
                throw new JsonFileManagerSaveException("Error while saving the medical record in JSON file");
            }
            log.info("<service> Medical record saved");
        }
    }


    /**
     * Update a medical record
     *
     * @param firstName     first name of the person to be updated
     * @param lastName      last name of the person to be updated
     * @param medicalRecord MedicalRecordUpdateDTO object to update
     * @return MedicalRecord object
     * @throws NotFoundException if medical record not found
     */
    public MedicalRecord updateMedicalRecord(String firstName, String lastName, MedicalRecordUpdateDTO medicalRecord) throws NotFoundException {
        log.info("<service> updateMedicalRecord");

        MedicalRecord medicalRecordUdated = medicalRecordRepository.updateMedicalRecord(firstName, lastName, medicalRecord);
        if (medicalRecordUdated == null) {
            log.info("<service> Medical record not found");
            throw new NotFoundException("Medical record not found with firstName: " + firstName + " and lastName: " + lastName);
        }

        log.info("<service> Medical record updated");

        return medicalRecordUdated;
    }
}
