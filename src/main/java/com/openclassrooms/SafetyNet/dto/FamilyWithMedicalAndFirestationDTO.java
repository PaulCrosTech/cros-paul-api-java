package com.openclassrooms.SafetyNet.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Schema(description = "Family (persons living at same address), with medical details and firestation")
@Data
public class FamilyWithMedicalAndFirestationDTO {

    @Schema(description = "Station number", example = "3", requiredMode = Schema.RequiredMode.REQUIRED)
    private String station;

    @Schema(description = "List of persons with medical details", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<PersonMedicalDetailsDTO> personMedicalDetailDTOS;

}
