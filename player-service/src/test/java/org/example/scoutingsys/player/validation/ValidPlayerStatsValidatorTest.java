package org.example.scoutingsys.player.validation;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.example.scoutingsys.player.dto.PlayerDto;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ValidPlayerStatsValidatorTest {

    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    private PlayerDto createValidBaseDto() {
        PlayerDto dto = new PlayerDto();
        dto.setPlayerName("Mykhailo");
        dto.setCountryId(1L);
        dto.setClubId(1L);
        return dto;
    }

    @Test
    @DisplayName("Adult player with any goals count passes validation")
    void validate_adultPlayerWithManyGoals_hasNoViolations() {
        PlayerDto dto = createValidBaseDto();
        dto.setAge(24);
        dto.setGoals(50);

        Set<ConstraintViolation<PlayerDto>> violations = validator.validate(dto);

        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Minor player (under 18) with goals <= 30 passes validation")
    void validate_minorPlayerWithPermittedGoals_hasNoViolations() {
        PlayerDto dto = createValidBaseDto();
        dto.setAge(17);
        dto.setGoals(25);

        Set<ConstraintViolation<PlayerDto>> violations = validator.validate(dto);

        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Boundary value: 17-year-old player with exactly 30 goals is valid")
    void validate_minorPlayerWithExactLimitGoals_hasNoViolations() {
        PlayerDto dto = createValidBaseDto();
        dto.setAge(17);
        dto.setGoals(30);

        Set<ConstraintViolation<PlayerDto>> violations = validator.validate(dto);

        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Minor player with > 30 goals fails with descriptive message")
    void validate_minorPlayerWithExcessiveGoals_returnsViolationWithExplainingMessage() {
        PlayerDto dto = createValidBaseDto();
        dto.setAge(16);
        dto.setGoals(45);

        Set<ConstraintViolation<PlayerDto>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
        ConstraintViolation<PlayerDto> violation = violations.iterator().next();
        
        assertTrue(violation.getMessage().contains("Minor player"));
        assertTrue(violation.getMessage().contains("45"));
    }

    @Test
    @DisplayName("Player under 14 fails with minimum age message")
    void validate_playerUnder14_returnsViolationWithExplainingMessage() {
        PlayerDto dto = createValidBaseDto();
        dto.setAge(12);
        dto.setGoals(5);

        Set<ConstraintViolation<PlayerDto>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
        ConstraintViolation<PlayerDto> violation = violations.iterator().next();
        assertTrue(violation.getMessage().contains("at least 14"));
    }

    @Test
    @DisplayName("Negative goals count fails with descriptive message")
    void validate_negativeGoals_returnsViolationWithExplainingMessage() {
        PlayerDto dto = createValidBaseDto();
        dto.setAge(20);
        dto.setGoals(-5);

        Set<ConstraintViolation<PlayerDto>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
        ConstraintViolation<PlayerDto> violation = violations.iterator().next();
        assertTrue(violation.getMessage().contains("negative"));
    }
}
