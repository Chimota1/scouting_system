package org.example.scoutingsys.mapper;

import org.example.scoutingsys.dto.LeagueDto;
import org.example.scoutingsys.model.League;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", uses = {ClubMapper.class},
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface LeagueMapper {
    @Mapping(source = "country.id", target = "countryId")
    LeagueDto toDto(League league);

    @Mapping(target = "country", ignore = true)
    @Mapping(target = "clubs", ignore = true)
    League toEntity(LeagueDto leagueDto);
}
