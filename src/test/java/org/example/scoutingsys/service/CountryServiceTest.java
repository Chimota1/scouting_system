package org.example.scoutingsys.service;

import jakarta.persistence.EntityNotFoundException;
import org.example.scoutingsys.dto.CountryDto;
import org.example.scoutingsys.mapper.CountryMapper;
import org.example.scoutingsys.model.Country;
import org.example.scoutingsys.repository.CountryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CountryServiceTest {

    @Mock
    private CountryRepository countryRepository;

    @Mock
    private CountryMapper countryMapper;

    @InjectMocks
    private CountryService countryService;

    private Country country;
    private CountryDto countryDto;

    @BeforeEach
    void setUp() {
        country = new Country();
        country.setId(1L);
        country.setCountryName("Ukraine");
        country.setContinent("Europe");

        countryDto = new CountryDto();
        countryDto.setId(1L);
        countryDto.setCountryName("Ukraine");
        countryDto.setContinent("Europe");
    }

    // ---------- create ----------

    @Test
    @DisplayName("create_validInput_returnsSavedDto")
    void create_validInput_returnsSavedDto() {
        CountryDto inputDto = new CountryDto();
        inputDto.setCountryName("Ukraine");
        inputDto.setContinent("Europe");

        Country entityToSave = new Country();
        entityToSave.setCountryName("Ukraine");
        entityToSave.setContinent("Europe");

        when(countryMapper.toEntity(inputDto)).thenReturn(entityToSave);
        when(countryRepository.save(entityToSave)).thenReturn(country);
        when(countryMapper.toDto(country)).thenReturn(countryDto);

        CountryDto result = countryService.create(inputDto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Ukraine", result.getCountryName());

        ArgumentCaptor<Country> captor = ArgumentCaptor.forClass(Country.class);
        verify(countryRepository).save(captor.capture());
        assertEquals("Ukraine", captor.getValue().getCountryName());
        assertEquals("Europe", captor.getValue().getContinent());
    }

    // ---------- getAll ----------

    @Test
    @DisplayName("getAll_countriesExist_returnsMappedDtoList")
    void getAll_countriesExist_returnsMappedDtoList() {
        when(countryRepository.findAll()).thenReturn(List.of(country));
        when(countryMapper.toDto(country)).thenReturn(countryDto);

        List<CountryDto> result = countryService.getAll();

        assertEquals(1, result.size());
        assertEquals("Ukraine", result.get(0).getCountryName());
        verify(countryRepository).findAll();
    }

    @Test
    @DisplayName("getAll_noCountries_returnsEmptyList")
    void getAll_noCountries_returnsEmptyList() {
        when(countryRepository.findAll()).thenReturn(List.of());

        List<CountryDto> result = countryService.getAll();

        assertTrue(result.isEmpty());
    }

    // ---------- getById ----------

    @Test
    @DisplayName("getById_existingId_returnsDto")
    void getById_existingId_returnsDto() {
        when(countryRepository.findById(1L)).thenReturn(Optional.of(country));
        when(countryMapper.toDto(country)).thenReturn(countryDto);

        CountryDto result = countryService.getById(1L);

        assertEquals(1L, result.getId());
    }

    @Test
    @DisplayName("getById_nonExistingId_throwsEntityNotFoundException")
    void getById_nonExistingId_throwsEntityNotFoundException() {
        when(countryRepository.findById(99L)).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                () -> countryService.getById(99L));
        assertTrue(ex.getMessage().contains("99"));

        verify(countryMapper, never()).toDto(any());
    }

    // ---------- update ----------

    @Test
    @DisplayName("update_existingId_updatesFieldsAndReturnsDto")
    void update_existingId_updatesFieldsAndReturnsDto() {
        // Arrange
        CountryDto updateDto = new CountryDto();
        updateDto.setCountryName("Poland");
        updateDto.setContinent("Europe");

        Country updatedEntity = new Country();
        updatedEntity.setId(1L);
        updatedEntity.setCountryName("Poland");
        updatedEntity.setContinent("Europe");

        CountryDto expectedDto = new CountryDto();
        expectedDto.setId(1L);
        expectedDto.setCountryName("Poland");
        expectedDto.setContinent("Europe");

        when(countryRepository.findById(1L)).thenReturn(Optional.of(country));
        when(countryRepository.save(country)).thenReturn(updatedEntity);
        when(countryMapper.toDto(updatedEntity)).thenReturn(expectedDto);

        // Act
        CountryDto result = countryService.update(1L, updateDto);

        // Assert
        assertEquals("Poland", result.getCountryName());

        ArgumentCaptor<Country> captor = ArgumentCaptor.forClass(Country.class);
        verify(countryRepository).save(captor.capture());
        assertEquals("Poland", captor.getValue().getCountryName());
        assertEquals("Europe", captor.getValue().getContinent());
    }

    @Test
    @DisplayName("update_nonExistingId_throwsEntityNotFoundException")
    void update_nonExistingId_throwsEntityNotFoundException() {
        when(countryRepository.findById(42L)).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                () -> countryService.update(42L, countryDto));
        assertTrue(ex.getMessage().contains("42"));

        verify(countryRepository, never()).save(any());
    }

    // ---------- delete ----------

    @Test
    @DisplayName("delete_existingId_deletesEntity")
    void delete_existingId_deletesEntity() {
        when(countryRepository.existsById(1L)).thenReturn(true);

        countryService.delete(1L);

        verify(countryRepository).deleteById(1L);
    }

    @Test
    @DisplayName("delete_nonExistingId_throwsEntityNotFoundException")
    void delete_nonExistingId_throwsEntityNotFoundException() {
        when(countryRepository.existsById(77L)).thenReturn(false);

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                () -> countryService.delete(77L));
        assertTrue(ex.getMessage().contains("77"));

        verify(countryRepository, never()).deleteById(any());
    }
}
