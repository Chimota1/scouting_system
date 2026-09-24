package org.example.scoutingsys.service;

import jakarta.persistence.EntityNotFoundException;
import org.example.scoutingsys.dto.ManagerDto;
import org.example.scoutingsys.mapper.ManagerMapper;
import org.example.scoutingsys.model.Club;
import org.example.scoutingsys.model.Country;
import org.example.scoutingsys.model.Manager;
import org.example.scoutingsys.repository.ClubRepository;
import org.example.scoutingsys.repository.CountryRepository;
import org.example.scoutingsys.repository.ManagerRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ManagerService {
    private final ManagerRepository managerRepository;
    private final ManagerMapper managerMapper;
    private final ClubRepository clubRepository;
    private final CountryRepository countryRepository;

    public ManagerService(ManagerRepository managerRepository, ManagerMapper managerMapper,
                          ClubRepository clubRepository, CountryRepository countryRepository) {
        this.managerRepository = managerRepository;
        this.managerMapper = managerMapper;
        this.clubRepository = clubRepository;
        this.countryRepository = countryRepository;
    }

    public ManagerDto create(ManagerDto managerDto) {
        Manager manager = managerMapper.toEntity(managerDto);
        setRelations(manager, managerDto);

        return managerMapper.toDto(managerRepository.save(manager));
    }

    public List<ManagerDto> getAll() {
        return managerRepository.findAll().stream()
                .map(managerMapper::toDto)
                .toList();
    }

    public ManagerDto getById(Long id) {
        Manager manager = managerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Менеджер з ID " + id + " не знайдений"));
        return managerMapper.toDto(manager);
    }

    public ManagerDto update(Long id, ManagerDto managerDto) {
        Manager existingManager = managerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Менеджер з ID " + id + " не знайдений"));

        existingManager.setManagerName(managerDto.getManagerName());
        existingManager.setManagerAge(managerDto.getManagerAge());
        setRelations(existingManager, managerDto);

        return managerMapper.toDto(managerRepository.save(existingManager));
    }

    private void setRelations(Manager manager, ManagerDto dto) {
        Country country = countryRepository.findById(dto.getCountryId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Країна з ID " + dto.getCountryId() + " не знайдена"));
        manager.setCountry(country);

        if (dto.getClubId() != null) {
            Club club = clubRepository.findById(dto.getClubId())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Клуб з ID " + dto.getClubId() + " не знайдений"));
            manager.setClub(club);
        } else {
            manager.setClub(null);
        }
    }

    public void delete(Long id) {
        if (!managerRepository.existsById(id)) {
            throw new EntityNotFoundException("Менеджер з ID " + id + " не знайдений");
        }
        managerRepository.deleteById(id);
    }
}
