package org.example.scoutingsys.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.example.scoutingsys.annotation.ValidIdReference;

@Getter
@Setter
public class ManagerDto {
    private Long id;
    @NotBlank(message = "В тренера має бути ім'я")
    private String managerName;
    @NotNull(message = "В тренера має бути вік")
    private Integer managerAge;
    @ValidIdReference
    private Long clubId;
    @NotNull(message = "Країна є обов'язковою")
    @ValidIdReference
    private Long countryId;
}