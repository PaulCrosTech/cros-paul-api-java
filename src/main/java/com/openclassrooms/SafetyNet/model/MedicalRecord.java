package com.openclassrooms.SafetyNet.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.openclassrooms.SafetyNet.validators.annotations.ValidDate;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * MedicalRecord Class
 */
@Schema(description = "Details about a medical record")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MedicalRecord {

    @Schema(description = "First name of the person", example = "John", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "First name is mandatory")
    @Size(min = 1, max = 35, message = "First name should have at least {min} character and at most {max} characters")
    private String firstName;

    @Schema(description = "Last name of the person", example = "Boyd", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Last name is mandatory")
    @Size(min = 1, max = 35, message = "Last name should have at least {min} character and at most {max} characters")
    private String lastName;

    @Schema(description = "Birthdate of the person", type = "string", pattern = "MM/dd/yyyy", example = "12/31/1980",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM/dd/yyyy")
    @ValidDate()
    private String birthdate;

    @Schema(description = "List of medications", type = "array", example = "[\"aznol:350mg\", \"hydrapermazol:100mg\"]",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY, shape = JsonFormat.Shape.ARRAY)
    @NotNull(message = "Medications is mandatory")
    private List<String> medications;

    @Schema(description = "List of allergies", type = "array", example = "[\"nillacilan\"]",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY, shape = JsonFormat.Shape.ARRAY)
    @NotNull(message = "Allergies is mandatory")
    private List<String> allergies;

    /**
     * Set the first name of the person capitalized
     *
     * @param firstName The first name of the person
     */
    public void setFirstName(String firstName) {
        this.firstName = StringUtils.capitalize(firstName);
    }

    /**
     * Set the last name of the person capitalized
     *
     * @param lastName The last name of the person
     */
    public void setLastName(String lastName) {
        this.lastName = StringUtils.capitalize(lastName);
    }
}
