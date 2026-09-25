package org.example.scoutingsys.controller;

import jakarta.persistence.EntityNotFoundException;
import org.example.scoutingsys.dto.LeagueDto;
import org.example.scoutingsys.service.LeagueService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class LeagueControllerTest {

    @Mock
    private LeagueService leagueService;

    @InjectMocks
    private LeagueController leagueController;

    private LeagueDto leagueDto;

    @BeforeEach
    void setUp() {
        leagueDto = new LeagueDto();
        leagueDto.setId(1L);
        leagueDto.setLeagueName("UPL");
        leagueDto.setCountryId(10L);
    }

    @Test
    @DisplayName("getAll_leaguesExist_returnsOkWithList")
    void getAll_leaguesExist_returnsOkWithList() {
        when(leagueService.getAll()).thenReturn(List.of(leagueDto));

        ResponseEntity<List<LeagueDto>> response = leagueController.getAll();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        verify(leagueService).getAll();
    }

    @Test
    @DisplayName("getById_existingId_returnsOkWithDto")
    void getById_existingId_returnsOkWithDto() {
        when(leagueService.getById(1L)).thenReturn(leagueDto);

        ResponseEntity<LeagueDto> response = leagueController.getById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("UPL", response.getBody().getLeagueName());
    }

    @Test
    @DisplayName("getById_serviceThrowsNotFound_propagatesException")
    void getById_serviceThrowsNotFound_propagatesException() {
        when(leagueService.getById(404L)).thenThrow(new EntityNotFoundException("Ліга з ID 404 не знайдена"));

        assertThrows(EntityNotFoundException.class, () -> leagueController.getById(404L));
    }

    @Test
    @DisplayName("create_validDto_returnsCreatedWithDto")
    void create_validDto_returnsCreatedWithDto() {
        LeagueDto inputDto = new LeagueDto();
        inputDto.setLeagueName("UPL");
        inputDto.setCountryId(10L);

        when(leagueService.create(inputDto)).thenReturn(leagueDto);

        ResponseEntity<LeagueDto> response = leagueController.create(inputDto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(1L, response.getBody().getId());

        ArgumentCaptor<LeagueDto> captor = ArgumentCaptor.forClass(LeagueDto.class);
        verify(leagueService).create(captor.capture());
        assertEquals("UPL", captor.getValue().getLeagueName());
    }


    @Test
    @DisplayName("update_validDto_returnsOkWithDto")
    void update_validDto_returnsOkWithDto() {
        LeagueDto updateDto = new LeagueDto();
        updateDto.setLeagueName("La Liga");
        updateDto.setCountryId(10L);

        LeagueDto updatedDto = new LeagueDto();
        updatedDto.setId(1L);
        updatedDto.setLeagueName("La Liga");
        updatedDto.setCountryId(10L);

        when(leagueService.update(1L, updateDto)).thenReturn(updatedDto);

        ResponseEntity<LeagueDto> response = leagueController.update(1L, updateDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("La Liga", response.getBody().getLeagueName());
        verify(leagueService).update(1L, updateDto);
    }


    @Test
    @DisplayName("delete_existingId_returnsNoContent")
    void delete_existingId_returnsNoContent() {
        doNothing().when(leagueService).delete(1L);

        ResponseEntity<Void> response = leagueController.delete(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(leagueService).delete(1L);
    }

    @Test
    @DisplayName("delete_serviceThrowsNotFound_propagatesException")
    void delete_serviceThrowsNotFound_propagatesException() {
        doThrow(new EntityNotFoundException("Ліга з ID 88 не знайдена"))
                .when(leagueService).delete(88L);

        assertThrows(EntityNotFoundException.class, () -> leagueController.delete(88L));
    }
}
