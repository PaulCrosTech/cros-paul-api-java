package com.openclassrooms.SafetyNet.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Schema(description = "Details about a medical record")
@Data
public class MedicalRecord {
    @Schema(description = "First name of the person", example = "John", requiredMode = Schema.RequiredMode.REQUIRED)
    private String firstName;
    @Schema(description = "Last name of the person", example = "Boyd", requiredMode = Schema.RequiredMode.REQUIRED)
    private String lastName;

    @Schema(description = "Birthdate of the person", example = "03/06/1984", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private Date birthdate;

    @Schema(description = "List of medications", example = "[aznol:350mg, hydrapermazol:100mg]")
    private List<String> medications;
    @Schema(description = "List of allergies", example = "[nillacilan]")
    private List<String> allergies;
}
