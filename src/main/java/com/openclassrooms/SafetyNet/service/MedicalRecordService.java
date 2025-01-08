package com.openclassrooms.SafetyNet.service;

import com.openclassrooms.SafetyNet.exceptions.ConflictException;
import com.openclassrooms.SafetyNet.exceptions.JsonFileManagerSaveException;
import com.openclassrooms.SafetyNet.exceptions.NotFoundException;
import com.openclassrooms.SafetyNet.model.MedicalRecord;
import com.openclassrooms.SafetyNet.dto.MedicalRecordUpdateDTO;
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
        List<MedicalRecord> medicalRecords = medicalRecordRepository.getMedicalRecords();
        log.info("{} medical records found", medicalRecords.size());
        return medicalRecords;
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
        MedicalRecord medicalRecord = medicalRecordRepository.getMedicalRecordByFirstNameAndLastName(firstName, lastName);
        if (medicalRecord == null) {
            throw new NotFoundException("Medical record not found with firstName: " + firstName + " and lastName: " + lastName);
        }
        log.info("Medical record of {} {} found", firstName, lastName);
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
        try {
            boolean deleted = medicalRecordRepository.deleteMedicalRecordByFirstNameAndLastName(firstName, lastName);
            if (!deleted) {
                throw new NotFoundException("Medical record not found with firstName: " + firstName + " and lastName: " + lastName);
            }
        } catch (JsonFileManagerSaveException e) {
            throw new Exception("Error while deleting the medical record in JSON file, firstName: " + firstName + " and lastName: " + lastName);
        }
        log.info("Medical record of {} {} deleted", firstName, lastName);
    }

    /**
     * Save a medical record
     *
     * @param medicalRecord MedicalRecord object to be saved
     * @throws JsonFileManagerSaveException if an error occurs while saving the file
     * @throws ConflictException            if medical record already exist
     */
    public void saveMedicalRecord(MedicalRecord medicalRecord) throws JsonFileManagerSaveException, ConflictException {
        try {
            // Vérifie si le medical record existe déjà
            MedicalRecord medicalRecordExist = getMedicalRecordByFirstNameAndLastName(medicalRecord.getFirstName(), medicalRecord.getLastName());
            if (medicalRecordExist != null) {
                throw new ConflictException("Medical record already exist with firstName: " + medicalRecord.getFirstName() + " and lastName: " + medicalRecord.getLastName());
            }
        } catch (NotFoundException e) {
            // Création du medical record
            try {
                medicalRecordRepository.saveMedicalRecord(medicalRecord);
            } catch (JsonFileManagerSaveException ex) {
                throw new JsonFileManagerSaveException("Error while saving the medical record in JSON file");
            }
            log.info("Medical record of {} {} saved", medicalRecord.getFirstName(), medicalRecord.getLastName());
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
        MedicalRecord medicalRecordUdated = medicalRecordRepository.updateMedicalRecord(firstName, lastName, medicalRecord);
        if (medicalRecordUdated == null) {
            throw new NotFoundException("Medical record not found with firstName: " + firstName + " and lastName: " + lastName);
        }
        log.info("Medical record of {} {} updated", firstName, lastName);

        return medicalRecordUdated;
    }
}
