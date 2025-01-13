package com.openclassrooms.SafetyNet.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Schema(description = "Family details : persons (split info adult, children) living at the same address")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FamilyDTO {

    private List<AdultDTO> adults;
    private List<ChildrenDTO> children;

}
