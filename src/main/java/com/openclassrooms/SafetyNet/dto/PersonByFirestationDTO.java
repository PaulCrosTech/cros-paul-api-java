package com.openclassrooms.SafetyNet.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;


@Schema(description = "Details about a person")
@Data
@AllArgsConstructor
public class PersonByFirestationDTO {

    @Schema(description = "First name of the person", example = "John", requiredMode = Schema.RequiredMode.REQUIRED, minLength = 1, maxLength = 35)
    @NotBlank(message = "First name is mandatory")
    @Size(min = 1, max = 35, message = "First name should have at least {min} character and at most {max} characters")
    private String firstName;

    @Schema(description = "Last name of the person", example = "Boyd", requiredMode = Schema.RequiredMode.REQUIRED, minLength = 1, maxLength = 35)
    @NotBlank(message = "Last name is mandatory")
    @Size(min = 1, max = 35, message = "Last name should have at least {min} character and at most {max} characters")
    private String lastName;

    @Schema(description = "Address of the person", example = "1509 Culver St", requiredMode = Schema.RequiredMode.REQUIRED, minLength = 4, maxLength = 35)
    @NotBlank(message = "Address is mandatory")
    @Size(min = 4, max = 35, message = "Address should have at least {min} character and at most {max} characters")
    private String address;

    @Schema(description = "Phone number of the person", example = "841-874-6512", requiredMode = Schema.RequiredMode.REQUIRED, minLength = 10, maxLength = 14)
    @NotBlank(message = "Phone is mandatory")
    @Size(min = 10, max = 14, message = "Phone should have at least {min} characters and at most {max} characters")
    private String phone;

}
