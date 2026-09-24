package org.example.scoutingsys.service;

import jakarta.persistence.EntityNotFoundException;
import org.example.scoutingsys.dto.LeagueDto;
import org.example.scoutingsys.mapper.LeagueMapper;
import org.example.scoutingsys.model.Country;
import org.example.scoutingsys.model.League;
import org.example.scoutingsys.repository.CountryRepository;
import org.example.scoutingsys.repository.LeagueRepository;
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

/**
 * Unit-тести для {@link LeagueService}.
 * LeagueRepository та CountryRepository імітуються через Mockito (AC3), Spring-контекст не піднімається.
 */
@ExtendWith(MockitoExtension.class)
class LeagueServiceTest {

    @Mock
    private LeagueRepository leagueRepository;
    @Mock
    private LeagueMapper leagueMapper;
    @Mock
    private CountryRepository countryRepository;

    @InjectMocks
    private LeagueService leagueService;

    private Country country;
    private League league;
    private LeagueDto leagueDto;

    @BeforeEach
    void setUp() {
        country = new Country();
        country.setId(10L);
        country.setCountryName("Ukraine");

        league = new League();
        league.setId(1L);
        league.setLeagueName("UPL");
        league.setCountry(country);

        leagueDto = new LeagueDto();
        leagueDto.setId(1L);
        leagueDto.setLeagueName("UPL");
        leagueDto.setCountryId(10L);
    }

    // ---------- create ----------

    @Test
    @DisplayName("create_validInput_returnsSavedDtoWithCountry")
    void create_validInput_returnsSavedDtoWithCountry() {
        // Arrange
        LeagueDto inputDto = new LeagueDto();
        inputDto.setLeagueName("UPL");
        inputDto.setCountryId(10L);

        League entityToSave = new League();
        entityToSave.setLeagueName("UPL");

        when(leagueMapper.toEntity(inputDto)).thenReturn(entityToSave);
        when(countryRepository.findById(10L)).thenReturn(Optional.of(country));
        when(leagueRepository.save(entityToSave)).thenReturn(league);
        when(leagueMapper.toDto(league)).thenReturn(leagueDto);

        // Act
        LeagueDto result = leagueService.create(inputDto);

        // Assert
        assertNotNull(result);
        assertEquals("UPL", result.getLeagueName());
        assertEquals(10L, result.getCountryId());

        ArgumentCaptor<League> captor = ArgumentCaptor.forClass(League.class);
        verify(leagueRepository).save(captor.capture());
        assertEquals(country, captor.getValue().getCountry());
    }

    @Test
    @DisplayName("create_nonExistingCountryId_throwsEntityNotFoundException")
    void create_nonExistingCountryId_throwsEntityNotFoundException() {
        // Arrange
        LeagueDto inputDto = new LeagueDto();
        inputDto.setLeagueName("UPL");
        inputDto.setCountryId(999L);

        when(leagueMapper.toEntity(inputDto)).thenReturn(new League());
        when(countryRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                () -> leagueService.create(inputDto));
        assertTrue(ex.getMessage().contains("999"));

        verify(leagueRepository, never()).save(any());
    }

    // ---------- getAll ----------

    @Test
    @DisplayName("getAll_leaguesExist_returnsMappedDtoList")
    void getAll_leaguesExist_returnsMappedDtoList() {
        // Arrange
        when(leagueRepository.findAll()).thenReturn(List.of(league));
        when(leagueMapper.toDto(league)).thenReturn(leagueDto);

        // Act
        List<LeagueDto> result = leagueService.getAll();

        // Assert
        assertEquals(1, result.size());
        assertEquals("UPL", result.get(0).getLeagueName());
    }

    @Test
    @DisplayName("getAll_noLeagues_returnsEmptyList")
    void getAll_noLeagues_returnsEmptyList() {
        // Arrange
        when(leagueRepository.findAll()).thenReturn(List.of());

        // Act
        List<LeagueDto> result = leagueService.getAll();

        // Assert
        assertTrue(result.isEmpty());
    }

    // ---------- getById ----------

    @Test
    @DisplayName("getById_existingId_returnsDto")
    void getById_existingId_returnsDto() {
        // Arrange
        when(leagueRepository.findById(1L)).thenReturn(Optional.of(league));
        when(leagueMapper.toDto(league)).thenReturn(leagueDto);

        // Act
        LeagueDto result = leagueService.getById(1L);

        // Assert
        assertEquals(1L, result.getId());
    }

    @Test
    @DisplayName("getById_nonExistingId_throwsEntityNotFoundException")
    void getById_nonExistingId_throwsEntityNotFoundException() {
        // Arrange
        when(leagueRepository.findById(404L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> leagueService.getById(404L));
        verify(leagueMapper, never()).toDto(any());
    }

    // ---------- update ----------

    @Test
    @DisplayName("update_existingLeagueAndCountry_updatesAndReturnsDto")
    void update_existingLeagueAndCountry_updatesAndReturnsDto() {
        // Arrange
        LeagueDto updateDto = new LeagueDto();
        updateDto.setLeagueName("La Liga");
        updateDto.setCountryId(10L);

        League updated = new League();
        updated.setId(1L);
        updated.setLeagueName("La Liga");
        updated.setCountry(country);

        LeagueDto expected = new LeagueDto();
        expected.setId(1L);
        expected.setLeagueName("La Liga");
        expected.setCountryId(10L);

        when(leagueRepository.findById(1L)).thenReturn(Optional.of(league));
        when(countryRepository.findById(10L)).thenReturn(Optional.of(country));
        when(leagueRepository.save(league)).thenReturn(updated);
        when(leagueMapper.toDto(updated)).thenReturn(expected);

        // Act
        LeagueDto result = leagueService.update(1L, updateDto);

        // Assert
        assertEquals("La Liga", result.getLeagueName());

        ArgumentCaptor<League> captor = ArgumentCaptor.forClass(League.class);
        verify(leagueRepository).save(captor.capture());
        assertEquals("La Liga", captor.getValue().getLeagueName());
        assertEquals(country, captor.getValue().getCountry());
    }

    @Test
    @DisplayName("update_nonExistingLeagueId_throwsEntityNotFoundException")
    void update_nonExistingLeagueId_throwsEntityNotFoundException() {
        // Arrange
        when(leagueRepository.findById(55L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> leagueService.update(55L, leagueDto));
        verify(leagueRepository, never()).save(any());
    }

    @Test
    @DisplayName("update_nonExistingCountryId_throwsEntityNotFoundException")
    void update_nonExistingCountryId_throwsEntityNotFoundException() {
        // Arrange
        LeagueDto updateDto = new LeagueDto();
        updateDto.setLeagueName("La Liga");
        updateDto.setCountryId(500L);

        when(leagueRepository.findById(1L)).thenReturn(Optional.of(league));
        when(countryRepository.findById(500L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> leagueService.update(1L, updateDto));
        verify(leagueRepository, never()).save(any());
    }

    // ---------- delete ----------

    @Test
    @DisplayName("delete_existingId_deletesEntity")
    void delete_existingId_deletesEntity() {
        // Arrange
        when(leagueRepository.existsById(1L)).thenReturn(true);

        // Act
        leagueService.delete(1L);

        // Assert
        verify(leagueRepository).deleteById(1L);
    }

    @Test
    @DisplayName("delete_nonExistingId_throwsEntityNotFoundException")
    void delete_nonExistingId_throwsEntityNotFoundException() {
        // Arrange
        when(leagueRepository.existsById(88L)).thenReturn(false);

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> leagueService.delete(88L));
        verify(leagueRepository, never()).deleteById(any());
    }
}
