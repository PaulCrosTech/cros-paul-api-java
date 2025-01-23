package com.openclassrooms.SafetyNet.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.logging.log4j.core.config.plugins.validation.constraints.NotBlank;

/**
 * Firestation Class
 */
@Schema(description = "Details about a firestation")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Firestation {

    @Schema(description = "Address of the firestation", example = "1509 Culver St", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Address is mandatory")
    @Size(min = 4, max = 35, message = "Address should have at least {min} character and at most {max} characters")
    private String address;

    @Schema(description = "Station number", example = "3", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Station number is mandatory")
    @Positive(message = "Station number should be positive")
    private Integer station;

}
