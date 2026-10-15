package org.example.scoutingsys.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;
import org.example.scoutingsys.annotation.ValidPlayerStats;

@Getter
@Setter
@ValidPlayerStats
public class PlayerDto {
    private Long id;
    @NotBlank(message = "Ім'я гравця не може бути порожнім")
    private String playerName;
    @NotNull(message = "Вік є обов'язковим")
    private Integer age;
    @NotNull(message = "Кількість голів є обов'язковою")
    private Integer goals;
    @Positive(message = "ID клубу має бути більшим за нуль")
    private Long clubId;
    @NotNull(message = "Країна є обов'язковою")
    @Positive(message = "ID країни має бути більшим за нуль")
    private Long countryId;
}