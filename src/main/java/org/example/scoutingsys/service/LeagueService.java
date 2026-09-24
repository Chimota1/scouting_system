package org.example.scoutingsys.service;

import jakarta.persistence.EntityNotFoundException;
import org.example.scoutingsys.dto.LeagueDto;
import org.example.scoutingsys.mapper.LeagueMapper;
import org.example.scoutingsys.model.Country;
import org.example.scoutingsys.model.League;
import org.example.scoutingsys.repository.CountryRepository;
import org.example.scoutingsys.repository.LeagueRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LeagueService {
    private final LeagueRepository leagueRepository;
    private final LeagueMapper leagueMapper;
    private final CountryRepository countryRepository;

    public LeagueService(LeagueRepository leagueRepository, LeagueMapper leagueMapper,
                         CountryRepository countryRepository) {
        this.leagueRepository = leagueRepository;
        this.leagueMapper = leagueMapper;
        this.countryRepository = countryRepository;
    }

    public LeagueDto create(LeagueDto leagueDto) {
        League league = leagueMapper.toEntity(leagueDto);
        Country country = countryRepository.findById(leagueDto.getCountryId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Країна з ID " + leagueDto.getCountryId() + " не знайдена"));
        league.setCountry(country);

        return leagueMapper.toDto(leagueRepository.save(league));
    }

    public List<LeagueDto> getAll() {
        return leagueRepository.findAll().stream()
                .map(leagueMapper::toDto)
                .toList();
    }

    public LeagueDto getById(Long id) {
        League league = leagueRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Ліга з ID " + id + " не знайдена"));
        return leagueMapper.toDto(league);
    }

    public LeagueDto update(Long id, LeagueDto leagueDto) {
        League existingLeague = leagueRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Ліга з ID " + id + " не знайдена"));

        existingLeague.setLeagueName(leagueDto.getLeagueName());

        Country country = countryRepository.findById(leagueDto.getCountryId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Країна з ID " + leagueDto.getCountryId() + " не знайдена"));
        existingLeague.setCountry(country);

        return leagueMapper.toDto(leagueRepository.save(existingLeague));
    }

    public void delete(Long id) {
        if (!leagueRepository.existsById(id)) {
            throw new EntityNotFoundException("Ліга з ID " + id + " не знайдена");
        }
        leagueRepository.deleteById(id);
    }
}
