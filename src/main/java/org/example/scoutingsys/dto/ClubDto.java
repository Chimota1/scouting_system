package org.example.scoutingsys.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.example.scoutingsys.annotation.ValidIdReference;

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
    @ValidIdReference
    private Long leagueId;
}