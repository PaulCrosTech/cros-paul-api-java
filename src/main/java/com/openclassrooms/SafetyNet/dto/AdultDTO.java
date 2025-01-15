package com.openclassrooms.SafetyNet.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * AdultDTO Class
 */
@Schema(description = "Details about an adult")
@Data
@AllArgsConstructor
public class AdultDTO {
    @Schema(description = "First name", example = "John", requiredMode = Schema.RequiredMode.REQUIRED, minLength = 1, maxLength = 35)
    private String firstName;

    @Schema(description = "Last name", example = "Boyd", requiredMode = Schema.RequiredMode.REQUIRED, minLength = 1, maxLength = 35)
    private String lastName;

}
