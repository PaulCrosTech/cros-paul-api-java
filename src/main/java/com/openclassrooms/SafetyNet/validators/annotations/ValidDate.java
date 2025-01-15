package com.openclassrooms.SafetyNet.validators.annotations;

import com.openclassrooms.SafetyNet.validators.DateValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * ValidDate Annotation
 */
@Constraint(validatedBy = DateValidator.class)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidDate {
    /**
     * message
     *
     * @return String
     */
    String message() default "Invalid date format";

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
    String pattern() default "MM/dd/yyyy";
}
