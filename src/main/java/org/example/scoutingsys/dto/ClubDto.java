package org.example.scoutingsys.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ClubDto {
    private Long id;
    @NotBlank
    private String clubName;
    private List<PlayerDto> players = new ArrayList<>();
    private ManagerDto manager;
    @NotNull
    private Long leagueId;
}