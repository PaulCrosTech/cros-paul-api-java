package com.openclassrooms.SafetyNet.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.HashMap;
import java.util.List;


@Schema(description = "Details about a person")
@Data
public class PersonWithMedicalDetailsGroupedByAddress {

    @Schema(description = "List of person, with medical details, grouped by address", requiredMode = Schema.RequiredMode.REQUIRED)
    HashMap<String, List<PersonMedicalDetailsWithPhone>> address;

}