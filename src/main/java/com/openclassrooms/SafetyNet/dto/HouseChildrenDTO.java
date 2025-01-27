package com.openclassrooms.SafetyNet.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * HouseChildrenDTO Class
 */
@Getter
@Setter
public class HouseChildrenDTO extends PersonWithAgeDTO {

    @JsonProperty("houseMembers")
    @Schema(description = "List of house members", example = "[{\"firstName\":\"Jacob\",\"lastName\":\"Boyd\",\"age\":40}]")
    private List<HouseMemberDTO> houseMembersDTO;

    public HouseChildrenDTO(String firstName, String lastName, int age, List<HouseMemberDTO> houseMembersDTO) {
        super(firstName, lastName, age);
        this.houseMembersDTO = houseMembersDTO;
    }

}
