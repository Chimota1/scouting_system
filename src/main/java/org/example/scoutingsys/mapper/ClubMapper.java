package org.example.scoutingsys.mapper;

import org.example.scoutingsys.dto.ClubDto;
import org.example.scoutingsys.model.Club;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", uses = {PlayerMapper.class, ManagerMapper.class},
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ClubMapper {
    @Mapping(source = "league.id", target = "leagueId")
    ClubDto toDto(Club club);

    @Mapping(target = "league", ignore = true)
    @Mapping(target = "players", ignore = true)
    @Mapping(target = "manager", ignore = true)
    Club toEntity(ClubDto clubDto);
}
