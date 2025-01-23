package com.openclassrooms.SafetyNet.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger Configuration
 */
@Configuration
@OpenAPIDefinition(
        info = @Info(title = "SafetyNet Alerts API", version = "1.0", description = "SafetyNet Alerts API")
)
public class SwaggerConfiguration {
}
