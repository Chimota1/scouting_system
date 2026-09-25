package org.example.scoutingsys.service;

import jakarta.persistence.EntityNotFoundException;
import org.example.scoutingsys.dto.PlayerDto;
import org.example.scoutingsys.mapper.PlayerMapper;
import org.example.scoutingsys.model.Club;
import org.example.scoutingsys.model.Country;
import org.example.scoutingsys.model.Player;
import org.example.scoutingsys.repository.ClubRepository;
import org.example.scoutingsys.repository.CountryRepository;
import org.example.scoutingsys.repository.PlayerRepository;
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
class PlayerServiceTest {

    @Mock
    private PlayerRepository playerRepository;
    @Mock
    private PlayerMapper playerMapper;
    @Mock
    private ClubRepository clubRepository;
    @Mock
    private CountryRepository countryRepository;

    @InjectMocks
    private PlayerService playerService;

    private Country country;
    private Club club;
    private Player player;
    private PlayerDto playerDto;

    @BeforeEach
    void setUp() {
        country = new Country();
        country.setId(3L);
        country.setCountryName("Ukraine");

        club = new Club();
        club.setId(2L);
        club.setClubName("Dynamo");

        player = new Player();
        player.setId(1L);
        player.setPlayerName("Andriy");
        player.setAge(25);
        player.setGoals(10);
        player.setCountry(country);

        playerDto = new PlayerDto();
        playerDto.setId(1L);
        playerDto.setPlayerName("Andriy");
        playerDto.setAge(25);
        playerDto.setGoals(10);
        playerDto.setCountryId(3L);
    }

    @Test
    @DisplayName("create_validInputWithClub_returnsSavedDto")
    void create_validInputWithClub_returnsSavedDto() {
        PlayerDto inputDto = new PlayerDto();
        inputDto.setPlayerName("Andriy");
        inputDto.setAge(25);
        inputDto.setGoals(10);
        inputDto.setCountryId(3L);
        inputDto.setClubId(2L);

        Player entityToSave = new Player();
        entityToSave.setPlayerName("Andriy");
        entityToSave.setAge(25);
        entityToSave.setGoals(10);

        when(playerMapper.toEntity(inputDto)).thenReturn(entityToSave);
        when(countryRepository.findById(3L)).thenReturn(Optional.of(country));
        when(clubRepository.findById(2L)).thenReturn(Optional.of(club));
        when(playerRepository.save(entityToSave)).thenReturn(player);
        when(playerMapper.toDto(player)).thenReturn(playerDto);

        PlayerDto result = playerService.create(inputDto);

        assertNotNull(result);
        assertEquals("Andriy", result.getPlayerName());
        assertEquals(10, result.getGoals());

        ArgumentCaptor<Player> captor = ArgumentCaptor.forClass(Player.class);
        verify(playerRepository).save(captor.capture());
        assertEquals(country, captor.getValue().getCountry());
        assertEquals(club, captor.getValue().getClub());
    }

    @Test
    @DisplayName("create_validInputWithoutClub_setsClubToNull")
    void create_validInputWithoutClub_setsClubToNull() {
        PlayerDto inputDto = new PlayerDto();
        inputDto.setPlayerName("Andriy");
        inputDto.setAge(25);
        inputDto.setGoals(10);
        inputDto.setCountryId(3L);
        inputDto.setClubId(null);

        Player entityToSave = new Player();

        when(playerMapper.toEntity(inputDto)).thenReturn(entityToSave);
        when(countryRepository.findById(3L)).thenReturn(Optional.of(country));
        when(playerRepository.save(entityToSave)).thenReturn(player);
        when(playerMapper.toDto(player)).thenReturn(playerDto);

        playerService.create(inputDto);

        ArgumentCaptor<Player> captor = ArgumentCaptor.forClass(Player.class);
        verify(playerRepository).save(captor.capture());
        assertNull(captor.getValue().getClub());
        verify(clubRepository, never()).findById(any());
    }

    @Test
    @DisplayName("create_nonExistingCountryId_throwsEntityNotFoundException")
    void create_nonExistingCountryId_throwsEntityNotFoundException() {
        PlayerDto inputDto = new PlayerDto();
        inputDto.setCountryId(999L);

        when(playerMapper.toEntity(inputDto)).thenReturn(new Player());
        when(countryRepository.findById(999L)).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                () -> playerService.create(inputDto));
        assertTrue(ex.getMessage().contains("999"));

        verify(playerRepository, never()).save(any());
    }

    @Test
    @DisplayName("create_nonExistingClubId_throwsEntityNotFoundException")
    void create_nonExistingClubId_throwsEntityNotFoundException() {
        PlayerDto inputDto = new PlayerDto();
        inputDto.setCountryId(3L);
        inputDto.setClubId(404L);

        when(playerMapper.toEntity(inputDto)).thenReturn(new Player());
        when(countryRepository.findById(3L)).thenReturn(Optional.of(country));
        when(clubRepository.findById(404L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> playerService.create(inputDto));
        verify(playerRepository, never()).save(any());
    }


    @Test
    @DisplayName("getAll_playersExist_returnsMappedDtoList")
    void getAll_playersExist_returnsMappedDtoList() {
        when(playerRepository.findAll()).thenReturn(List.of(player));
        when(playerMapper.toDto(player)).thenReturn(playerDto);

        List<PlayerDto> result = playerService.getAll();

        assertEquals(1, result.size());
        assertEquals("Andriy", result.get(0).getPlayerName());
    }

    @Test
    @DisplayName("getAll_noPlayers_returnsEmptyList")
    void getAll_noPlayers_returnsEmptyList() {
        when(playerRepository.findAll()).thenReturn(List.of());

        List<PlayerDto> result = playerService.getAll();

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("getById_existingId_returnsDto")
    void getById_existingId_returnsDto() {
        when(playerRepository.findById(1L)).thenReturn(Optional.of(player));
        when(playerMapper.toDto(player)).thenReturn(playerDto);

        PlayerDto result = playerService.getById(1L);

        assertEquals(1L, result.getId());
    }

    @Test
    @DisplayName("getById_nonExistingId_throwsEntityNotFoundException")
    void getById_nonExistingId_throwsEntityNotFoundException() {
        when(playerRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> playerService.getById(2L));
        verify(playerMapper, never()).toDto(any());
    }

    @Test
    @DisplayName("update_existingId_updatesFieldsAndRelationsAndReturnsDto")
    void update_existingId_updatesFieldsAndRelationsAndReturnsDto() {
        PlayerDto updateDto = new PlayerDto();
        updateDto.setPlayerName("Oleh");
        updateDto.setAge(30);
        updateDto.setGoals(20);
        updateDto.setCountryId(3L);
        updateDto.setClubId(2L);

        Player updated = new Player();
        updated.setId(1L);
        updated.setPlayerName("Oleh");
        updated.setAge(30);
        updated.setGoals(20);

        PlayerDto expected = new PlayerDto();
        expected.setId(1L);
        expected.setPlayerName("Oleh");
        expected.setAge(30);
        expected.setGoals(20);

        when(playerRepository.findById(1L)).thenReturn(Optional.of(player));
        when(countryRepository.findById(3L)).thenReturn(Optional.of(country));
        when(clubRepository.findById(2L)).thenReturn(Optional.of(club));
        when(playerRepository.save(player)).thenReturn(updated);
        when(playerMapper.toDto(updated)).thenReturn(expected);

        PlayerDto result = playerService.update(1L, updateDto);

        assertEquals("Oleh", result.getPlayerName());
        assertEquals(20, result.getGoals());

        ArgumentCaptor<Player> captor = ArgumentCaptor.forClass(Player.class);
        verify(playerRepository).save(captor.capture());
        assertEquals("Oleh", captor.getValue().getPlayerName());
        assertEquals(30, captor.getValue().getAge());
        assertEquals(20, captor.getValue().getGoals());
        assertEquals(club, captor.getValue().getClub());
    }

    @Test
    @DisplayName("update_nonExistingId_throwsEntityNotFoundException")
    void update_nonExistingId_throwsEntityNotFoundException() {

        when(playerRepository.findById(15L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> playerService.update(15L, playerDto));
        verify(playerRepository, never()).save(any());
    }

    @Test
    @DisplayName("delete_existingId_deletesEntity")
    void delete_existingId_deletesEntity() {
        when(playerRepository.existsById(1L)).thenReturn(true);

        playerService.delete(1L);

        verify(playerRepository).deleteById(1L);
    }

    @Test
    @DisplayName("delete_nonExistingId_throwsEntityNotFoundException")
    void delete_nonExistingId_throwsEntityNotFoundException() {
        when(playerRepository.existsById(66L)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> playerService.delete(66L));
        verify(playerRepository, never()).deleteById(any());
    }
}
