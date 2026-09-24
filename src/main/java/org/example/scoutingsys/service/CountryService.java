package org.example.scoutingsys.service;

import jakarta.persistence.EntityNotFoundException;
import org.example.scoutingsys.dto.CountryDto;
import org.example.scoutingsys.mapper.CountryMapper;
import org.example.scoutingsys.model.Country;
import org.example.scoutingsys.repository.CountryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CountryService {
    private final CountryRepository countryRepository;
    private final CountryMapper countryMapper;

    public CountryService(CountryRepository countryRepository, CountryMapper countryMapper) {
        this.countryRepository = countryRepository;
        this.countryMapper = countryMapper;
    }

    public CountryDto create(CountryDto countryDto) {
        Country country = countryMapper.toEntity(countryDto);
        return countryMapper.toDto(countryRepository.save(country));
    }

    public List<CountryDto> getAll() {
        return countryRepository.findAll().stream()
                .map(countryMapper::toDto)
                .toList();
    }

    public CountryDto getById(Long id) {
        Country country = countryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Країна з ID " + id + " не знайдена"));
        return countryMapper.toDto(country);
    }

    public CountryDto update(Long id, CountryDto countryDto) {
        Country existingCountry = countryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Країна з ID " + id + " не знайдена"));

        existingCountry.setCountryName(countryDto.getCountryName());
        existingCountry.setContinent(countryDto.getContinent());

        return countryMapper.toDto(countryRepository.save(existingCountry));
    }

    public void delete(Long id) {
        if (!countryRepository.existsById(id)) {
            throw new EntityNotFoundException("Країна з ID " + id + " не знайдена");
        }
        countryRepository.deleteById(id);
    }
}
