package com.openclassrooms.SafetyNet.utils;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "com.openclassrooms.safetynetalert")
@Data
public class CustomProperties {
    private String jsonFilePath;
}
