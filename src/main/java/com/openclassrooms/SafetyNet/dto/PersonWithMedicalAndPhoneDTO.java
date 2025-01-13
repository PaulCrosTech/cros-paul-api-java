package com.openclassrooms.SafetyNet.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Schema(description = "Details about a person with medical record and phone")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PersonWithMedicalAndPhoneDTO {

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

    @Schema(description = "Phone number of the person", example = "841-874-6512", requiredMode = Schema.RequiredMode.REQUIRED, minLength = 10, maxLength = 14)
    private String phone;

}
