package com.openclassrooms.SafetyNet.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * CustomProperties Class
 */
@Configuration
@ConfigurationProperties(prefix = "com.openclassrooms.safetynetalert")
@Data
public class CustomProperties {
    private String jsonFilePath;
}
