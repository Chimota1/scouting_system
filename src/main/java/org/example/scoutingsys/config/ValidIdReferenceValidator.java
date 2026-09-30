package org.example.scoutingsys.config;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.example.scoutingsys.annotation.ValidIdReference;

public class ValidIdReferenceValidator implements ConstraintValidator<ValidIdReference, Long> {

    @Override
    public boolean isValid(Long value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        return value > 0;
    }
}