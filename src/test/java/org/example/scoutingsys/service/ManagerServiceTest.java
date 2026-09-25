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
class ManagerServiceTest {

    @Mock
    private ManagerRepository managerRepository;
    @Mock
    private ManagerMapper managerMapper;
    @Mock
    private ClubRepository clubRepository;
    @Mock
    private CountryRepository countryRepository;

    @InjectMocks
    private ManagerService managerService;

    private Country country;
    private Club club;
    private Manager manager;
    private ManagerDto managerDto;

    @BeforeEach
    void setUp() {
        country = new Country();
        country.setId(3L);
        country.setCountryName("Ukraine");

        club = new Club();
        club.setId(2L);
        club.setClubName("Dynamo");

        manager = new Manager();
        manager.setId(1L);
        manager.setManagerName("Coach");
        manager.setManagerAge(45);
        manager.setCountry(country);

        managerDto = new ManagerDto();
        managerDto.setId(1L);
        managerDto.setManagerName("Coach");
        managerDto.setManagerAge(45);
        managerDto.setCountryId(3L);
    }

    @Test
    @DisplayName("create_validInputWithClub_returnsSavedDto")
    void create_validInputWithClub_returnsSavedDto() {
        ManagerDto inputDto = new ManagerDto();
        inputDto.setManagerName("Coach");
        inputDto.setManagerAge(45);
        inputDto.setCountryId(3L);
        inputDto.setClubId(2L);

        Manager entityToSave = new Manager();
        entityToSave.setManagerName("Coach");
        entityToSave.setManagerAge(45);

        when(managerMapper.toEntity(inputDto)).thenReturn(entityToSave);
        when(countryRepository.findById(3L)).thenReturn(Optional.of(country));
        when(clubRepository.findById(2L)).thenReturn(Optional.of(club));
        when(managerRepository.save(entityToSave)).thenReturn(manager);
        when(managerMapper.toDto(manager)).thenReturn(managerDto);

        ManagerDto result = managerService.create(inputDto);

        assertNotNull(result);
        assertEquals("Coach", result.getManagerName());

        ArgumentCaptor<Manager> captor = ArgumentCaptor.forClass(Manager.class);
        verify(managerRepository).save(captor.capture());
        assertEquals(country, captor.getValue().getCountry());
        assertEquals(club, captor.getValue().getClub());
    }

    @Test
    @DisplayName("create_validInputWithoutClub_setsClubToNull")
    void create_validInputWithoutClub_setsClubToNull() {
        ManagerDto inputDto = new ManagerDto();
        inputDto.setManagerName("Coach");
        inputDto.setManagerAge(45);
        inputDto.setCountryId(3L);
        inputDto.setClubId(null);

        Manager entityToSave = new Manager();

        when(managerMapper.toEntity(inputDto)).thenReturn(entityToSave);
        when(countryRepository.findById(3L)).thenReturn(Optional.of(country));
        when(managerRepository.save(entityToSave)).thenReturn(manager);
        when(managerMapper.toDto(manager)).thenReturn(managerDto);

        managerService.create(inputDto);

        ArgumentCaptor<Manager> captor = ArgumentCaptor.forClass(Manager.class);
        verify(managerRepository).save(captor.capture());
        assertNull(captor.getValue().getClub());
        verify(clubRepository, never()).findById(any());
    }

    @Test
    @DisplayName("create_nonExistingCountryId_throwsEntityNotFoundException")
    void create_nonExistingCountryId_throwsEntityNotFoundException() {
        ManagerDto inputDto = new ManagerDto();
        inputDto.setCountryId(999L);

        when(managerMapper.toEntity(inputDto)).thenReturn(new Manager());
        when(countryRepository.findById(999L)).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                () -> managerService.create(inputDto));
        assertTrue(ex.getMessage().contains("999"));

        verify(managerRepository, never()).save(any());
    }

    @Test
    @DisplayName("create_nonExistingClubId_throwsEntityNotFoundException")
    void create_nonExistingClubId_throwsEntityNotFoundException() {
        ManagerDto inputDto = new ManagerDto();
        inputDto.setCountryId(3L);
        inputDto.setClubId(404L);

        when(managerMapper.toEntity(inputDto)).thenReturn(new Manager());
        when(countryRepository.findById(3L)).thenReturn(Optional.of(country));
        when(clubRepository.findById(404L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> managerService.create(inputDto));
        verify(managerRepository, never()).save(any());
    }

    @Test
    @DisplayName("getAll_managersExist_returnsMappedDtoList")
    void getAll_managersExist_returnsMappedDtoList() {
        when(managerRepository.findAll()).thenReturn(List.of(manager));
        when(managerMapper.toDto(manager)).thenReturn(managerDto);

        List<ManagerDto> result = managerService.getAll();

        assertEquals(1, result.size());
        assertEquals("Coach", result.get(0).getManagerName());
    }

    @Test
    @DisplayName("getAll_noManagers_returnsEmptyList")
    void getAll_noManagers_returnsEmptyList() {
        when(managerRepository.findAll()).thenReturn(List.of());

        List<ManagerDto> result = managerService.getAll();

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("getById_existingId_returnsDto")
    void getById_existingId_returnsDto() {
        when(managerRepository.findById(1L)).thenReturn(Optional.of(manager));
        when(managerMapper.toDto(manager)).thenReturn(managerDto);

        ManagerDto result = managerService.getById(1L);

        assertEquals(1L, result.getId());
    }

    @Test
    @DisplayName("getById_nonExistingId_throwsEntityNotFoundException")
    void getById_nonExistingId_throwsEntityNotFoundException() {
        when(managerRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> managerService.getById(2L));
        verify(managerMapper, never()).toDto(any());
    }

    @Test
    @DisplayName("update_existingId_updatesFieldsAndRelationsAndReturnsDto")
    void update_existingId_updatesFieldsAndRelationsAndReturnsDto() {
        ManagerDto updateDto = new ManagerDto();
        updateDto.setManagerName("New Coach");
        updateDto.setManagerAge(50);
        updateDto.setCountryId(3L);
        updateDto.setClubId(2L);

        Manager updated = new Manager();
        updated.setId(1L);
        updated.setManagerName("New Coach");
        updated.setManagerAge(50);

        ManagerDto expected = new ManagerDto();
        expected.setId(1L);
        expected.setManagerName("New Coach");
        expected.setManagerAge(50);

        when(managerRepository.findById(1L)).thenReturn(Optional.of(manager));
        when(countryRepository.findById(3L)).thenReturn(Optional.of(country));
        when(clubRepository.findById(2L)).thenReturn(Optional.of(club));
        when(managerRepository.save(manager)).thenReturn(updated);
        when(managerMapper.toDto(updated)).thenReturn(expected);

        ManagerDto result = managerService.update(1L, updateDto);

        assertEquals("New Coach", result.getManagerName());
        assertEquals(50, result.getManagerAge());

        ArgumentCaptor<Manager> captor = ArgumentCaptor.forClass(Manager.class);
        verify(managerRepository).save(captor.capture());
        assertEquals("New Coach", captor.getValue().getManagerName());
        assertEquals(50, captor.getValue().getManagerAge());
        assertEquals(club, captor.getValue().getClub());
    }

    @Test
    @DisplayName("update_nonExistingId_throwsEntityNotFoundException")
    void update_nonExistingId_throwsEntityNotFoundException() {
        when(managerRepository.findById(15L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> managerService.update(15L, managerDto));
        verify(managerRepository, never()).save(any());
    }

    @Test
    @DisplayName("delete_existingId_deletesEntity")
    void delete_existingId_deletesEntity() {
        when(managerRepository.existsById(1L)).thenReturn(true);

        managerService.delete(1L);

        verify(managerRepository).deleteById(1L);
    }

    @Test
    @DisplayName("delete_nonExistingId_throwsEntityNotFoundException")
    void delete_nonExistingId_throwsEntityNotFoundException() {
        when(managerRepository.existsById(66L)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> managerService.delete(66L));
        verify(managerRepository, never()).deleteById(any());
    }
}
