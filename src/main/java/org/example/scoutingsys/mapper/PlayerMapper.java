package org.example.scoutingsys.mapper;

import org.example.scoutingsys.dto.PlayerDto;
import org.example.scoutingsys.model.Player;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PlayerMapper {
    @Mapping(source = "country.id", target = "countryId")
    @Mapping(source = "club.id", target = "clubId")
    PlayerDto toDto(Player player);

    @Mapping(target = "country", ignore = true)
    @Mapping(target = "club", ignore = true)
    Player toEntity(PlayerDto playerDto);
}
