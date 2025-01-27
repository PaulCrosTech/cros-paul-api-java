package com.openclassrooms.SafetyNet.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


/**
 * PersonCoveredByStationDTO Class
 */
@Schema(description = "Details about persons covered by station, with number of children and adults")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PersonCoveredByStationDTO {

    @Schema(description = "Number of childrens", example = "0")
    private int nbChildren;

    @Schema(description = "Number of adults", example = "1")
    private int nbAdults;

    @Schema(description = "List of persons covered by the station")
    private List<PersonBasicDetailsDTO> persons;

}

