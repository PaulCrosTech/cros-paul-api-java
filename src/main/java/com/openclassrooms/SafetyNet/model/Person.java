package com.openclassrooms.SafetyNet.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "Details about a person")
@Data
public class Person {
    @Schema(description = "First name of the person", example = "John", requiredMode = Schema.RequiredMode.REQUIRED)
    private String firstName;
    @Schema(description = "Last name of the person", example = "Boyd", requiredMode = Schema.RequiredMode.REQUIRED)
    private String lastName;
    @Schema(description = "Address of the person", example = "1509 Culver St", requiredMode = Schema.RequiredMode.REQUIRED)
    private String address;
    @Schema(description = "City of the person", example = "Culver", requiredMode = Schema.RequiredMode.REQUIRED)
    private String city;
    @Schema(description = "Zip code of the person", example = "97451", requiredMode = Schema.RequiredMode.REQUIRED)
    private String zip;
    @Schema(description = "Phone number of the person", example = "841-874-6512", requiredMode = Schema.RequiredMode.REQUIRED)
    private String phone;
    @Schema(description = "Email of the person", example = "drk@email.com", requiredMode = Schema.RequiredMode.REQUIRED)
    private String email;
}
