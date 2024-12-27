package com.openclassrooms.SafetyNet.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "Details about a firestation")
@Data
public class Firestation {
    @Schema(description = "Address of the firestation", example = "1509 Culver St", requiredMode = Schema.RequiredMode.REQUIRED)
    private String address;
    @Schema(description = "Station number", example = "3", requiredMode = Schema.RequiredMode.REQUIRED)
    private int station;
}
