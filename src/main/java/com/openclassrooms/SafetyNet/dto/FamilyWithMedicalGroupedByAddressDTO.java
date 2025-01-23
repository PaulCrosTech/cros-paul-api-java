package com.openclassrooms.SafetyNet.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.List;


/**
 * FamilyWithMedicalGroupedByAddressDTO Class
 */
@Schema(description = "Family (persons at same address) with medical details, grouped by address")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FamilyWithMedicalGroupedByAddressDTO {

    HashMap<String, List<PersonWithMedicalAndPhoneDTO>> mapAddressPersons;

}