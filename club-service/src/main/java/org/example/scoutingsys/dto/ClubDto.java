package org.example.scoutingsys.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
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
    private ManagerDto manager;
    @NotNull
    @Positive(message = "ID ліги має бути більшим за нуль")
    private Long leagueId;
}