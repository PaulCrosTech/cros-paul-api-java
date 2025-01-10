package com.openclassrooms.SafetyNet.validators;

import com.openclassrooms.SafetyNet.validators.annotations.ValidPhone;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;


public class PhoneValidator implements ConstraintValidator<ValidPhone, String> {

    private String pattern;

    @Override
    public void initialize(ValidPhone constraintAnnotation) {
        this.pattern = constraintAnnotation.pattern();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        return Pattern.matches(pattern, value);
    }
}