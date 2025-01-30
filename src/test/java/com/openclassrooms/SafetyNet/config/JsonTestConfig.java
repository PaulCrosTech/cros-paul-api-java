package com.openclassrooms.SafetyNet.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.SafetyNet.utils.JsonModel;
import com.openclassrooms.SafetyNet.utils.JsonFileManager;

import java.io.File;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

/**
 * JsonTestConfig class
 */
public class JsonTestConfig {

    /**
     * Path to json test file
     */
    private static final String PATH_TO_JSON_TEST_FILE = "src/test/java/com/openclassrooms/SafetyNet/ressources/datas_for_tests.json";

    /**
     * Load specific json file for tests
     *
     * @param jsonFileManager JsonFileManager
     * @throws Exception exception
     */
    public static void loadJsonTest(JsonFileManager jsonFileManager) throws Exception {
        // Load specific json file for tests
        ObjectMapper objectMapper = new ObjectMapper();
        File jsonFile = new File(PATH_TO_JSON_TEST_FILE);
        when(jsonFileManager.getPersons()).thenReturn(objectMapper.readValue(jsonFile, JsonModel.class).getPersons());
        when(jsonFileManager.getFirestations()).thenReturn(objectMapper.readValue(jsonFile, JsonModel.class).getFirestations());
        when(jsonFileManager.getMedicalRecords()).thenReturn(objectMapper.readValue(jsonFile, JsonModel.class).getMedicalrecords());

        // Do nothing when saveJsonFile is called
        doNothing().when(jsonFileManager).saveJsonFile();
    }
}
