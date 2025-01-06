package com.openclassrooms.SafetyNet.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Schema(description = "Details about occupants of an address")
@Data
public class AddressOccupants {

    private List<Adult> adults;
    private List<Children> childrens;
}
