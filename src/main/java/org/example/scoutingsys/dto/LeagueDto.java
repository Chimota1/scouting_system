package org.example.scoutingsys.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LeagueDto {
    private Long id;
    @NotBlank
    private String leagueName;
    @NotNull
    @Positive(message = "ID країни має бути більшим за нуль")
    private Long countryId;
}