package com.openclassrooms.SafetyNet.validators;

import com.openclassrooms.SafetyNet.validators.annotations.ValidPhone;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

/**
 * PhoneValidator Class
 */
public class PhoneValidator implements ConstraintValidator<ValidPhone, String> {

    private String pattern;

    /**
     * Initialize the phone pattern
     *
     * @param constraintAnnotation the phone pattern
     */
    @Override
    public void initialize(ValidPhone constraintAnnotation) {
        this.pattern = constraintAnnotation.pattern();
    }

    /**
     * Check if the phone number is valid
     *
     * @param value   the phone number
     * @param context the context
     * @return true if the phone number is valid
     */
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        return Pattern.matches(pattern, value);
    }
}