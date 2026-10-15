package org.example.scoutingsys.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import org.example.scoutingsys.config.ValidPlayerStatsValidator;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidPlayerStatsValidator.class)
@Documented
public @interface ValidPlayerStats {

    String message() default "Invalid player statistics: minors under 18 cannot exceed 30 goals";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
