package com.openclassrooms.SafetyNet.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.SafetyNet.exceptions.JsonFileManagerLoadException;
import com.openclassrooms.SafetyNet.exceptions.JsonFileManagerSaveException;
import com.openclassrooms.SafetyNet.model.Firestation;
import com.openclassrooms.SafetyNet.model.MedicalRecord;
import com.openclassrooms.SafetyNet.model.Person;

import com.openclassrooms.SafetyNet.config.CustomProperties;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;

/**
 * JsonFileManager Class
 */
@Log4j2
@Component
public class JsonFileManager {

    private JsonModel jsonModel;

    private final ObjectMapper objectMapper;
    private final CustomProperties customProperties;

    /**
     * Constructor
     *
     * @param objectMapper     ObjectMapper
     * @param customProperties CustomProperties
     */
    public JsonFileManager(ObjectMapper objectMapper, CustomProperties customProperties) {
        log.info("<constructor> JsonFileManager");
        this.objectMapper = objectMapper;
        this.customProperties = customProperties;

        loadJsonFile();
    }

    /**
     * Load the Json file into a JsonModel object
     *
     * @throws JsonFileManagerLoadException if an error occurs while loading the file
     */
    private void loadJsonFile() {
        try {
            jsonModel = objectMapper.readValue(new File(customProperties.getJsonFilePath()), JsonModel.class);
            log.info("Json file loaded");

        } catch (Exception e) {
            throw new JsonFileManagerLoadException("Error while loading the file");
        }
    }

    /**
     * Save the JsonModel object into the Json file
     *
     * @throws JsonFileManagerSaveException if an error occurs while saving the file
     */
    public void saveJsonFile() throws JsonFileManagerSaveException {
        try {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(customProperties.getJsonFilePath()), jsonModel);
            log.info("JSON file saved");

        } catch (Exception e) {
            throw new JsonFileManagerSaveException("Error while saving the file");
        }
    }

    /**
     * Get the list of persons from the JsonModel object
     *
     * @return List of Person objects
     */
    public List<Person> getPersons() {
        return jsonModel.getPersons();
    }

    /**
     * Get the list of firestations from the JsonModel object
     *
     * @return List of Firestation objects
     */
    public List<Firestation> getFirestations() {
        return jsonModel.getFirestations();
    }

    /**
     * Get the list of medical records from the JsonModel object
     *
     * @return List of MedicalRecord objects
     */
    public List<MedicalRecord> getMedicalRecords() {
        return jsonModel.getMedicalrecords();
    }

}
