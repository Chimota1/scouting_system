package org.example.scoutingsys.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class CountryDto {
    private Long id;
    @NotBlank
    private String countryName;
    @NotBlank
    private String continent;
    private List<PlayerDto> players = new ArrayList<>();
    private List<LeagueDto> leagues = new ArrayList<>();
    private List<ManagerDto> managers = new ArrayList<>();
}