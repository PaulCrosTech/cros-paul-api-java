package com.openclassrooms.SafetyNet.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Schema(description = "Updatable informations about a firestation")
@Data
public class FirestationUpdateDTO {

    @Schema(description = "Station number", example = "3", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Station number is mandatory")
    @Positive(message = "Station number should be positive")
    private String station;

}
