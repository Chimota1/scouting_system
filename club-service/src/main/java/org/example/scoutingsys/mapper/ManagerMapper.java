package org.example.scoutingsys.mapper;

import org.example.scoutingsys.dto.ManagerDto;
import org.example.scoutingsys.model.Manager;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ManagerMapper {
    @Mapping(source = "country.id", target = "countryId")
    @Mapping(source = "club.id", target = "clubId")
    ManagerDto toDto(Manager manager);

    @Mapping(target = "country", ignore = true)
    @Mapping(target = "club", ignore = true)
    Manager toEntity(ManagerDto managerDto);
}
