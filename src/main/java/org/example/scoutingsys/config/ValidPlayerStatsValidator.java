package org.example.scoutingsys.config;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.example.scoutingsys.annotation.ValidPlayerStats;
import org.example.scoutingsys.dto.PlayerDto;

public class ValidPlayerStatsValidator implements ConstraintValidator<ValidPlayerStats, PlayerDto> {

    @Override
    public boolean isValid(PlayerDto dto, ConstraintValidatorContext context) {
        if (dto == null) {
            return true;
        }

        Integer age = dto.getAge();
        Integer goals = dto.getGoals();

        // Null checks are handled by @NotNull
        if (age == null || goals == null) {
            return true;
        }

        // Rule 1: Minimum player registration age is 14
        if (age < 14) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                    "Player age must be at least 14 (provided: " + age + ")"
            ).addPropertyNode("age").addConstraintViolation();
            return false;
        }
        
        if (goals < 0) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                    "Goals cannot be negative (provided: " + goals + ")"
            ).addPropertyNode("goals").addConstraintViolation();
            return false;
        }

        if (age < 18 && goals > 30) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                    "Minor player (age " + age + ") cannot exceed 30 goals (provided: " + goals + ")"
            ).addPropertyNode("goals").addConstraintViolation();
            return false;
        }

        return true;
    }
}
