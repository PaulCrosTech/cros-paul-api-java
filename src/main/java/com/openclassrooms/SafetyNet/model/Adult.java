package com.openclassrooms.SafetyNet.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "Details about an adult")
@Data
public class Adult {
    @Schema(description = "First name", example = "John", requiredMode = Schema.RequiredMode.REQUIRED, minLength = 1, maxLength = 35)
    private String firstName;

    @Schema(description = "Last name", example = "Boyd", requiredMode = Schema.RequiredMode.REQUIRED, minLength = 1, maxLength = 35)
    private String lastName;

}
