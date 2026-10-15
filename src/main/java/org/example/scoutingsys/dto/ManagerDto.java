package org.example.scoutingsys.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ManagerDto {
    private Long id;
    @NotBlank(message = "В тренера має бути ім'я")
    private String managerName;
    @NotNull(message = "В тренера має бути вік")
    private Integer managerAge;
    @Positive(message = "ID клубу має бути більшим за нуль")
    private Long clubId;
    @NotNull(message = "Країна є обов'язковою")
    @Positive(message = "ID країни має бути більшим за нуль")
    private Long countryId;
}