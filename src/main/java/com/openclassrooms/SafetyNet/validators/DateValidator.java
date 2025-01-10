package com.openclassrooms.SafetyNet.validators;

import com.openclassrooms.SafetyNet.validators.annotations.ValidDate;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.text.ParseException;
import java.text.SimpleDateFormat;


public class DateValidator implements ConstraintValidator<ValidDate, String> {

    private String pattern;

    @Override
    public void initialize(ValidDate constraintAnnotation) {
        this.pattern = constraintAnnotation.pattern();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true; // null values are valid, use @NotNull for null checks
        }
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        sdf.setLenient(false);
        try {
            sdf.parse(value);
        } catch (ParseException e) {
            return false;
        }
        return true;
    }
}