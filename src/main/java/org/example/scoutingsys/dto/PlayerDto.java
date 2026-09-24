package org.example.scoutingsys.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlayerDto {
    private Long id;
    @NotBlank(message = "Ім'я гравця не може бути порожнім")
    private String playerName;
    @NotNull(message = "Вік є обов'язковим")
    private Integer age;
    @NotNull(message = "Кількість голів є обов'язковою")
    private Integer goals;
    private Long clubId;
    @NotNull(message = "Країна є обов'язковою")
    private Long countryId;
}