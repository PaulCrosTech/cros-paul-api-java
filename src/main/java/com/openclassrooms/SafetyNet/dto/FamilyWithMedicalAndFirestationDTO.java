package com.openclassrooms.SafetyNet.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * FamilyWithMedicalAndFirestationDTO Class
 */
@Schema(description = "Family (persons living at same address), with medical details and fire station")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FamilyWithMedicalAndFirestationDTO {

    @Schema(description = "Fire stations number", example = "3", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer station;

    @Schema(description = "List of persons with medical details", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<PersonMedicalDetailsDTO> personMedicalDetailDTOS;

}
