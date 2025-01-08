package com.openclassrooms.SafetyNet.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;


@Schema(description = "Details about persons covered by station, with number of children and adults")
@Data
public class PersonCoveredByStationDTO {

    @Schema(description = "Number of childrens", example = "2")
    private int nbChildren;

    @Schema(description = "Number of adults", example = "3")
    private int nbAdults;

    @Schema(description = "List of persons covered by the station")
    private List<PersonBasicDetailsDTO> persons;

}

