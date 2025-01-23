package com.openclassrooms.SafetyNet.validators;

import com.openclassrooms.SafetyNet.validators.annotations.ValidDate;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.text.ParseException;
import java.text.SimpleDateFormat;


/**
 * DateValidator Class
 */
public class DateValidator implements ConstraintValidator<ValidDate, String> {

    private String pattern;

    /**
     * Initialize the pattern
     *
     * @param constraintAnnotation the annotation
     */
    @Override
    public void initialize(ValidDate constraintAnnotation) {
        this.pattern = constraintAnnotation.pattern();
    }

    /**
     * Check if the date is valid
     *
     * @param value   the date
     * @param context the context
     * @return true if the date is valid
     */
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