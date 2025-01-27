package com.openclassrooms.SafetyNet.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * PersonWithMedicalAndEmailDTO Class
 */
@Schema(description = "Details about a person with medical record and email")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PersonWithMedicalAndEmailDTO {

    @Schema(description = "First name of the person", example = "John", requiredMode = Schema.RequiredMode.REQUIRED)
    private String firstName;

    @Schema(description = "Last name of the person", example = "Boyd", requiredMode = Schema.RequiredMode.REQUIRED)
    private String lastName;

    @Schema(description = "Age", example = "10", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer age;

    @Schema(description = "Email of the person", example = "mail@email.com", requiredMode = Schema.RequiredMode.REQUIRED, minLength = 5, maxLength = 255)
    private String email;

    @Schema(description = "List of medications", type = "array", example = "[\"aznol:350mg\", \"hydrapermazol:100mg\"]",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private List<String> medications;

    @Schema(description = "List of allergies", type = "array", example = "[\"nillacilan\"]",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private List<String> allergies;


}
