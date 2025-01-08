package com.openclassrooms.SafetyNet.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Schema(description = "Details about a medical record")
@Data
@AllArgsConstructor
public class PersonMedicalDetails {

    @Schema(description = "First name of the person", example = "John", requiredMode = Schema.RequiredMode.REQUIRED)
    private String firstName;

    @Schema(description = "Last name of the person", example = "Boyd", requiredMode = Schema.RequiredMode.REQUIRED)
    private String lastName;

    @Schema(description = "List of medications", type = "array", example = "[\"aznol:350mg\", \"hydrapermazol:100mg\"]",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private List<String> medications;

    @Schema(description = "List of allergies", type = "array", example = "[\"nillacilan\"]",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private List<String> allergies;

    @Schema(description = "Age", example = "10", requiredMode = Schema.RequiredMode.REQUIRED)
    private int age;
}
