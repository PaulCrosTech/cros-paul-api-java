package com.openclassrooms.SafetyNet.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Schema(description = "Details about a medical record")
@Data
public class MedicalRecord {

    @Schema(description = "First name of the person", example = "John", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "First name is mandatory")
    @Size(min = 1, max = 35, message = "First name should have at least {min} character and at most {max} characters")
    private String firstName;

    @Schema(description = "Last name of the person", example = "Boyd", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Last name is mandatory")
    @Size(min = 1, max = 35, message = "Last name should have at least {min} character and at most {max} characters")
    private String lastName;

    @Schema(description = "Birthdate of the person", example = "03/06/1984", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    @Past(message = "Birthdate should be in the past")
    private Date birthdate;

    @Schema(description = "List of medications", example = "[aznol:350mg, hydrapermazol:100mg]")
    private List<String> medications;

    @Schema(description = "List of allergies", example = "[nillacilan]")
    private List<String> allergies;
}
