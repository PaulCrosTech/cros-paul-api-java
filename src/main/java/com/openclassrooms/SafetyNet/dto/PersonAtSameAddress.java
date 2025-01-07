package com.openclassrooms.SafetyNet.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Schema(description = "Details about occupants of an address")
@Data
public class PersonAtSameAddress {

    private List<Adult> adults;
    private List<Children> childrens;
}
