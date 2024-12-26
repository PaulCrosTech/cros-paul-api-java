package com.openclassrooms.SafetyNet.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.SafetyNet.exceptions.JsonFileServiceLoadException;
import com.openclassrooms.SafetyNet.model.Firestation;
import com.openclassrooms.SafetyNet.model.JsonModel;
import com.openclassrooms.SafetyNet.model.MedicalRecord;
import com.openclassrooms.SafetyNet.model.Person;

import com.openclassrooms.SafetyNet.config.CustomProperties;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.util.List;

@Log4j2
@Repository
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
    @Autowired
    public JsonFileManager(ObjectMapper objectMapper, CustomProperties customProperties) {
        this.objectMapper = objectMapper;
        this.customProperties = customProperties;

        loadJsonFile();
    }

    /**
     * Load the Json file into a JsonModel object
     */
    private void loadJsonFile() {
        try {
            jsonModel = objectMapper.readValue(new File(customProperties.getJsonFilePath()), JsonModel.class);
            log.info("==> Json : file loaded");

        } catch (Exception e) {
            log.error("==> Json : error while loading the file");
            throw new JsonFileServiceLoadException("Error while loading the file");
        }
    }

    public List<Person> getPersons() {
        return jsonModel.getPersons();
    }

    public List<Firestation> getFirestations() {
        return jsonModel.getFirestations();
    }

    public List<MedicalRecord> getMedicalRecords() {
        return jsonModel.getMedicalrecords();
    }

}
