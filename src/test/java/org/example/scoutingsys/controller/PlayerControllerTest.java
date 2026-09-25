package org.example.scoutingsys.controller;

import jakarta.persistence.EntityNotFoundException;
import org.example.scoutingsys.dto.PlayerDto;
import org.example.scoutingsys.service.PlayerService;
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
class PlayerControllerTest {

    @Mock
    private PlayerService playerService;

    @InjectMocks
    private PlayerController playerController;

    private PlayerDto playerDto;

    @BeforeEach
    void setUp() {
        playerDto = new PlayerDto();
        playerDto.setId(1L);
        playerDto.setPlayerName("Andriy");
        playerDto.setAge(25);
        playerDto.setGoals(10);
        playerDto.setCountryId(3L);
    }

    @Test
    @DisplayName("getAll_playersExist_returnsOkWithList")
    void getAll_playersExist_returnsOkWithList() {
        when(playerService.getAll()).thenReturn(List.of(playerDto));

        ResponseEntity<List<PlayerDto>> response = playerController.getAll();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals("Andriy", response.getBody().get(0).getPlayerName());
        verify(playerService).getAll();
    }

    @Test
    @DisplayName("getAll_noPlayers_returnsOkWithEmptyList")
    void getAll_noPlayers_returnsOkWithEmptyList() {
        when(playerService.getAll()).thenReturn(List.of());

        ResponseEntity<List<PlayerDto>> response = playerController.getAll();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isEmpty());
    }

    @Test
    @DisplayName("getById_existingId_returnsOkWithDto")
    void getById_existingId_returnsOkWithDto() {
        when(playerService.getById(1L)).thenReturn(playerDto);

        ResponseEntity<PlayerDto> response = playerController.getById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Andriy", response.getBody().getPlayerName());
    }

    @Test
    @DisplayName("getById_serviceThrowsNotFound_propagatesException")
    void getById_serviceThrowsNotFound_propagatesException() {
        when(playerService.getById(2L)).thenThrow(new EntityNotFoundException("Гравець з ID 2 не знайдений"));

        assertThrows(EntityNotFoundException.class, () -> playerController.getById(2L));
    }

    @Test
    @DisplayName("create_validDto_returnsCreatedWithDto")
    void create_validDto_returnsCreatedWithDto() {
        PlayerDto inputDto = new PlayerDto();
        inputDto.setPlayerName("Andriy");
        inputDto.setAge(25);
        inputDto.setGoals(10);
        inputDto.setCountryId(3L);

        when(playerService.create(inputDto)).thenReturn(playerDto);

        ResponseEntity<PlayerDto> response = playerController.create(inputDto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(1L, response.getBody().getId());

        ArgumentCaptor<PlayerDto> captor = ArgumentCaptor.forClass(PlayerDto.class);
        verify(playerService).create(captor.capture());
        assertEquals("Andriy", captor.getValue().getPlayerName());
        assertEquals(10, captor.getValue().getGoals());
    }

    @Test
    @DisplayName("update_validDto_returnsOkWithDto")
    void update_validDto_returnsOkWithDto() {
        PlayerDto updateDto = new PlayerDto();
        updateDto.setPlayerName("Oleh");
        updateDto.setAge(30);
        updateDto.setGoals(20);
        updateDto.setCountryId(3L);

        PlayerDto updatedDto = new PlayerDto();
        updatedDto.setId(1L);
        updatedDto.setPlayerName("Oleh");
        updatedDto.setAge(30);
        updatedDto.setGoals(20);

        when(playerService.update(1L, updateDto)).thenReturn(updatedDto);

        ResponseEntity<PlayerDto> response = playerController.update(1L, updateDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Oleh", response.getBody().getPlayerName());
        assertEquals(20, response.getBody().getGoals());
        verify(playerService).update(1L, updateDto);
    }

    @Test
    @DisplayName("update_serviceThrowsNotFound_propagatesException")
    void update_serviceThrowsNotFound_propagatesException() {
        PlayerDto updateDto = new PlayerDto();
        when(playerService.update(15L, updateDto))
                .thenThrow(new EntityNotFoundException("Гравець з ID 15 не знайдений"));

        assertThrows(EntityNotFoundException.class, () -> playerController.update(15L, updateDto));
    }

    @Test
    @DisplayName("delete_existingId_returnsNoContent")
    void delete_existingId_returnsNoContent() {
        doNothing().when(playerService).delete(1L);

        ResponseEntity<Void> response = playerController.delete(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(playerService).delete(1L);
    }

    @Test
    @DisplayName("delete_serviceThrowsNotFound_propagatesException")
    void delete_serviceThrowsNotFound_propagatesException() {
        doThrow(new EntityNotFoundException("Гравець з ID 66 не знайдений"))
                .when(playerService).delete(66L);

        assertThrows(EntityNotFoundException.class, () -> playerController.delete(66L));
    }
}
