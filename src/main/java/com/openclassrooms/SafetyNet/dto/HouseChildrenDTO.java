package com.openclassrooms.SafetyNet.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * HouseChildrenDTO Class
 */
@Getter
@Setter
public class HouseChildrenDTO extends PersonWithAgeDTO {

    private List<HouseMemberDTO> houseMembersDTO;

    public HouseChildrenDTO(String firstName, String lastName, int age, List<HouseMemberDTO> houseMembersDTO) {
        super(firstName, lastName, age);
        this.houseMembersDTO = houseMembersDTO;
    }
    
}
