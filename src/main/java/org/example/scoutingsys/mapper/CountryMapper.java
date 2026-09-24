package org.example.scoutingsys.mapper;

import org.example.scoutingsys.dto.CountryDto;
import org.example.scoutingsys.model.Country;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", uses = {PlayerMapper.class, ManagerMapper.class, LeagueMapper.class},
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CountryMapper {
    CountryDto toDto(Country country);

    @Mapping(target = "players", ignore = true)
    @Mapping(target = "leagues", ignore = true)
    @Mapping(target = "managers", ignore = true)
    Country toEntity(CountryDto countryDto);
}
