package org.example.scoutingsys.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidIdReferenceValidator.class)
@Documented

public @interface ValidIdReference {
    String message() default "Ідентифікатор пов'язаної сутності має бути більшим за нуль (зламаний або відсутній зв'язок)";

    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
