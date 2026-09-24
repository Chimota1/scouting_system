package org.example.scoutingsys.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class LeagueDto {
    private Long id;
    @NotBlank
    private String leagueName;
    @NotNull
    private Long countryId;
    private List<ClubDto> clubs = new ArrayList<>();
}