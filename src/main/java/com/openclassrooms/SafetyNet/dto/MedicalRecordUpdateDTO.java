package com.openclassrooms.SafetyNet.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Schema(description = "Updatable informations about a medical record")
@Data
public class MedicalRecordUpdateDTO {

    @Schema(description = "Birthdate of the person", type = "string", pattern = "dd/MM/yyyy", example = "31/12/1980", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    @Past(message = "Birthdate should be in the past")
    private Date birthdate;

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
}
