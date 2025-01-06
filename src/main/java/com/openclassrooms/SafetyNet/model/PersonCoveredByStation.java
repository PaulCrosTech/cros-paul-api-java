package com.openclassrooms.SafetyNet.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;


@Schema(description = "Details about persons covered by station, with number of children and adults")
@Data
public class PersonCoveredByStation {

    @Schema(description = "Number of childrens", example = "2")
    private int nbChildrens;

    @Schema(description = "Number of adults", example = "3")
    private int nbAdults;

    @Schema(description = "List of persons covered by the station")
    private List<PersonByFirestationDTO> personsCovered;


}
