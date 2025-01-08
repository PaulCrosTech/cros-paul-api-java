package com.openclassrooms.SafetyNet.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.HashMap;
import java.util.List;


@Schema(description = "Family (persons at same address) with medical details, grouped by address")
@Data
public class FamilyWithMedicalGroupedByAddressDTO {

    HashMap<String, List<PersonWithMedicalAndPhoneDTO>> address;

}