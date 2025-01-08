package com.openclassrooms.SafetyNet.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Schema(description = "Details about persons living at the same address")
@Data
public class FamilyDTO {

    private List<AdultDTO> adults;
    private List<ChildrenDTO> children;
}
