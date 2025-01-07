package com.openclassrooms.SafetyNet.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
public class PersonAtSameAddressWithMedicalDetailsAndFirestation {

    @Schema(description = "Station number", example = "3", requiredMode = Schema.RequiredMode.REQUIRED)
    private String station;

    @Schema(description = "List of persons at the same address", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<PersonMedicalDetails> personMedicalDetails;

}
