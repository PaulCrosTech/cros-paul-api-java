package com.openclassrooms.SafetyNet.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.SafetyNet.exceptions.JsonFileServiceLoadException;
import com.openclassrooms.SafetyNet.model.JsonModel;
import com.openclassrooms.SafetyNet.model.Person;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;

@Service
public class JsonManager {

    private JsonModel jsonModel;

    @Autowired
    private CustomProperties customProperties;

    @Autowired
    private ObjectMapper objectMapper;


    /**
     * Load the Json file into a JsonModel object
     */
    private void load() {

        try {
            jsonModel = objectMapper.readValue(new File(customProperties.getJsonFilePath()), JsonModel.class);

        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new JsonFileServiceLoadException("Error loading the Json file");
        }
    }

    public List<Person> getPersons() {
        load();
        return jsonModel.getPersons();
    }

}
