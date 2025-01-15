package com.openclassrooms.SafetyNet.validators.annotations;


import com.openclassrooms.SafetyNet.validators.PhoneValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * ValidPhone Annotation
 */
@Constraint(validatedBy = PhoneValidator.class)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidPhone {
    /**
     * message
     *
     * @return String
     */
    String message() default "Phone number should be in the format xxx-xxx-xxxx";

    /**
     * groups
     *
     * @return Class
     */
    Class<?>[] groups() default {};

    /**
     * payload
     *
     * @return Class
     */
    Class<? extends Payload>[] payload() default {};

    /**
     * pattern
     *
     * @return String
     */
    String pattern() default "^\\d{3}-\\d{3}-\\d{4}$";
}
