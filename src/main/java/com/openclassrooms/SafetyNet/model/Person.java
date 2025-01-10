package com.openclassrooms.SafetyNet.model;

import com.openclassrooms.SafetyNet.validators.annotations.ValidPhone;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;


@Schema(description = "Details about a person")
@Data
public class Person {

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

    @Schema(description = "City of the person", example = "Culver", requiredMode = Schema.RequiredMode.REQUIRED, minLength = 2, maxLength = 35)
    @NotBlank(message = "City is mandatory")
    @Size(min = 2, max = 35, message = "City should have at least {min} character and at most {max} characters")
    private String city;

    @Schema(description = "Zip code of the person", example = "97451", requiredMode = Schema.RequiredMode.REQUIRED, minLength = 4, maxLength = 9)
    @NotBlank(message = "Zip is mandatory")
    @Size(min = 4, max = 9, message = "Zip should have at least {min} characters and at most {max} characters")
    private String zip;

    @Schema(description = "Phone number of the person", example = "841-874-6512", requiredMode = Schema.RequiredMode.REQUIRED, minLength = 10, maxLength = 14)
    @NotBlank(message = "Phone is mandatory")
    @ValidPhone()
    private String phone;

    @Schema(description = "Email of the person", example = "mail@email.com", requiredMode = Schema.RequiredMode.REQUIRED, minLength = 5, maxLength = 255)
    @NotBlank(message = "Email is mandatory")
    @Size(min = 5, max = 255, message = "Email should have at least {min} character and at most {max} characters")
    @Email(message = "Email should be valid")
    private String email;


    public void setFirstName(String firstName) {
        this.firstName = StringUtils.capitalize(firstName);
    }

    public void setLastName(String lastName) {
        this.lastName = StringUtils.capitalize(lastName);
    }

}
