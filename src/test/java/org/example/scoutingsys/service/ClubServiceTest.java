package org.example.scoutingsys.service;

import jakarta.persistence.EntityNotFoundException;
import org.example.scoutingsys.dto.ClubDto;
import org.example.scoutingsys.dto.ManagerDto;
import org.example.scoutingsys.mapper.ClubMapper;
import org.example.scoutingsys.model.Club;
import org.example.scoutingsys.model.League;
import org.example.scoutingsys.model.Manager;
import org.example.scoutingsys.repository.ClubRepository;
import org.example.scoutingsys.repository.LeagueRepository;
import org.example.scoutingsys.repository.ManagerRepository;
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
class ClubServiceTest {

    @Mock
    private ClubRepository clubRepository;
    @Mock
    private ClubMapper clubMapper;
    @Mock
    private LeagueRepository leagueRepository;
    @Mock
    private ManagerRepository managerRepository;

    @InjectMocks
    private ClubService clubService;

    private League league;
    private Club club;
    private ClubDto clubDto;

    @BeforeEach
    void setUp() {
        league = new League();
        league.setId(5L);
        league.setLeagueName("UPL");

        club = new Club();
        club.setId(1L);
        club.setClubName("Dynamo");
        club.setLeague(league);

        clubDto = new ClubDto();
        clubDto.setId(1L);
        clubDto.setClubName("Dynamo");
        clubDto.setLeagueId(5L);
    }

    @Test
    @DisplayName("create: успішно створює клуб без менеджера та повертає збережений DTO")
    void create_validInputWithoutManager_returnsSavedDto() {
        ClubDto inputDto = new ClubDto();
        inputDto.setClubName("Dynamo");
        inputDto.setLeagueId(5L);

        Club entityToSave = new Club();
        entityToSave.setClubName("Dynamo");

        when(clubMapper.toEntity(inputDto)).thenReturn(entityToSave);
        when(leagueRepository.findById(5L)).thenReturn(Optional.of(league));
        when(clubRepository.save(entityToSave)).thenReturn(club);
        when(clubMapper.toDto(club)).thenReturn(clubDto);

        ClubDto result = clubService.create(inputDto);

        assertNotNull(result);
        assertEquals("Dynamo", result.getClubName());
        verify(managerRepository, never()).save(any());
        verify(managerRepository, never()).findById(any());
    }

    @Test
    @DisplayName("create: викидає EntityNotFoundException, якщо лігу з вказаним leagueId не знайдено")
    void create_nonExistingLeagueId_throwsEntityNotFoundException() {
        ClubDto inputDto = new ClubDto();
        inputDto.setClubName("Dynamo");
        inputDto.setLeagueId(999L);

        when(clubMapper.toEntity(inputDto)).thenReturn(new Club());
        when(leagueRepository.findById(999L)).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                () -> clubService.create(inputDto));
        assertTrue(ex.getMessage().contains("999"));

        verify(clubRepository, never()).save(any());
    }

    @Test
    @DisplayName("create: успішно прив'язує існуючого менеджера до клубу та зберігає його")
    void create_withValidManagerId_assignsManagerToClubAndSavesManager() {
        ManagerDto managerDto = new ManagerDto();
        managerDto.setId(7L);

        ClubDto inputDto = new ClubDto();
        inputDto.setClubName("Dynamo");
        inputDto.setLeagueId(5L);
        inputDto.setManager(managerDto);

        Club entityToSave = new Club();
        entityToSave.setClubName("Dynamo");

        Manager manager = new Manager();
        manager.setId(7L);
        manager.setManagerName("Coach");

        when(clubMapper.toEntity(inputDto)).thenReturn(entityToSave);
        when(leagueRepository.findById(5L)).thenReturn(Optional.of(league));
        when(clubRepository.save(entityToSave)).thenReturn(club);
        when(managerRepository.findById(7L)).thenReturn(Optional.of(manager));
        when(clubMapper.toDto(club)).thenReturn(clubDto);

        ClubDto result = clubService.create(inputDto);

        assertNotNull(result);

        ArgumentCaptor<Manager> managerCaptor = ArgumentCaptor.forClass(Manager.class);
        verify(managerRepository).save(managerCaptor.capture());
        assertEquals(club, managerCaptor.getValue().getClub());
        assertEquals(manager, club.getManager());
    }

    @Test
    @DisplayName("create: викидає EntityNotFoundException, якщо менеджера з вказаним ID не знайдено")
    void create_withNonExistingManagerId_throwsEntityNotFoundException() {
        ManagerDto managerDto = new ManagerDto();
        managerDto.setId(404L);

        ClubDto inputDto = new ClubDto();
        inputDto.setClubName("Dynamo");
        inputDto.setLeagueId(5L);
        inputDto.setManager(managerDto);

        Club entityToSave = new Club();

        when(clubMapper.toEntity(inputDto)).thenReturn(entityToSave);
        when(leagueRepository.findById(5L)).thenReturn(Optional.of(league));
        when(clubRepository.save(entityToSave)).thenReturn(club);
        when(managerRepository.findById(404L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> clubService.create(inputDto));
        verify(managerRepository, never()).save(any());
    }


    @Test
    @DisplayName("getAll: повертає список змаплених DTO, якщо клуби є в базі даних")
    void getAll_clubsExist_returnsMappedDtoList() {
        when(clubRepository.findAll()).thenReturn(List.of(club));
        when(clubMapper.toDto(club)).thenReturn(clubDto);

        List<ClubDto> result = clubService.getAll();

        assertEquals(1, result.size());
        assertEquals("Dynamo", result.get(0).getClubName());
    }

    @Test
    @DisplayName("getAll: повертає порожній список, якщо клубів немає")
    void getAll_noClubs_returnsEmptyList() {
        when(clubRepository.findAll()).thenReturn(List.of());

        List<ClubDto> result = clubService.getAll();

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("getById: повертає DTO клубу за існуючим ID")
    void getById_existingId_returnsDto() {
        when(clubRepository.findById(1L)).thenReturn(Optional.of(club));
        when(clubMapper.toDto(club)).thenReturn(clubDto);

        ClubDto result = clubService.getById(1L);

        assertEquals(1L, result.getId());
    }

    @Test
    @DisplayName("getById: викидає EntityNotFoundException, якщо клуб з ID не знайдено")
    void getById_nonExistingId_throwsEntityNotFoundException() {
        when(clubRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> clubService.getById(2L));
        verify(clubMapper, never()).toDto(any());
    }

    @Test
    @DisplayName("update: успішно оновлює клуб і лігу та повертає оновлений DTO")
    void update_existingClubAndLeague_updatesAndReturnsDto() {
        ClubDto updateDto = new ClubDto();
        updateDto.setClubName("Shakhtar");
        updateDto.setLeagueId(5L);

        Club updated = new Club();
        updated.setId(1L);
        updated.setClubName("Shakhtar");
        updated.setLeague(league);

        ClubDto expected = new ClubDto();
        expected.setId(1L);
        expected.setClubName("Shakhtar");
        expected.setLeagueId(5L);

        when(clubRepository.findById(1L)).thenReturn(Optional.of(club));
        when(leagueRepository.findById(5L)).thenReturn(Optional.of(league));
        when(clubRepository.save(club)).thenReturn(updated);
        when(clubMapper.toDto(updated)).thenReturn(expected);

        ClubDto result = clubService.update(1L, updateDto);

        assertEquals("Shakhtar", result.getClubName());

        ArgumentCaptor<Club> captor = ArgumentCaptor.forClass(Club.class);
        verify(clubRepository).save(captor.capture());
        assertEquals("Shakhtar", captor.getValue().getClubName());
    }

    @Test
    @DisplayName("update: викидає EntityNotFoundException, якщо клуб для оновлення не знайдено")
    void update_nonExistingClubId_throwsEntityNotFoundException() {
        when(clubRepository.findById(9L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> clubService.update(9L, clubDto));
        verify(clubRepository, never()).save(any());
    }

    @Test
    @DisplayName("update: викидає EntityNotFoundException, якщо нову лігу не знайдено")
    void update_nonExistingLeagueId_throwsEntityNotFoundException() {
        ClubDto updateDto = new ClubDto();
        updateDto.setClubName("Shakhtar");
        updateDto.setLeagueId(600L);

        when(clubRepository.findById(1L)).thenReturn(Optional.of(club));
        when(leagueRepository.findById(600L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> clubService.update(1L, updateDto));
        verify(clubRepository, never()).save(any());
    }


    @Test
    @DisplayName("delete: успішно видаляє сутність за існуючим ID")
    void delete_existingId_deletesEntity() {
        when(clubRepository.existsById(1L)).thenReturn(true);

        clubService.delete(1L);

        verify(clubRepository).deleteById(1L);
    }

    @Test
    @DisplayName("delete: викидає EntityNotFoundException, якщо клуб для видалення не знайдено")
    void delete_nonExistingId_throwsEntityNotFoundException() {
        when(clubRepository.existsById(123L)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> clubService.delete(123L));
        verify(clubRepository, never()).deleteById(any());
    }
}
