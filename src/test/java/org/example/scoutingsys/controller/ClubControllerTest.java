package org.example.scoutingsys.controller;

import jakarta.persistence.EntityNotFoundException;
import org.example.scoutingsys.dto.ClubDto;
import org.example.scoutingsys.service.ClubService;
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
class ClubControllerTest {

    @Mock
    private ClubService clubService;

    @InjectMocks
    private ClubController clubController;

    private ClubDto clubDto;

    @BeforeEach
    void setUp() {
        clubDto = new ClubDto();
        clubDto.setId(1L);
        clubDto.setClubName("Dynamo");
        clubDto.setLeagueId(5L);
    }


    @Test
    @DisplayName("getAll_clubsExist_returnsOkWithList")
    void getAll_clubsExist_returnsOkWithList() {
        when(clubService.getAll()).thenReturn(List.of(clubDto));

        ResponseEntity<List<ClubDto>> response = clubController.getAll();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals("Dynamo", response.getBody().get(0).getClubName());
        verify(clubService).getAll();
    }


    @Test
    @DisplayName("getById_existingId_returnsOkWithDto")
    void getById_existingId_returnsOkWithDto() {
        when(clubService.getById(1L)).thenReturn(clubDto);

        ResponseEntity<ClubDto> response = clubController.getById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Dynamo", response.getBody().getClubName());
    }

    @Test
    @DisplayName("getById_serviceThrowsNotFound_propagatesException")
    void getById_serviceThrowsNotFound_propagatesException() {
        when(clubService.getById(99L)).thenThrow(new EntityNotFoundException("Клуб з ID 99 не знайдений"));

        assertThrows(EntityNotFoundException.class, () -> clubController.getById(99L));
    }

    @Test
    @DisplayName("create_validDto_returnsCreatedWithDto")
    void create_validDto_returnsCreatedWithDto() {
        ClubDto inputDto = new ClubDto();
        inputDto.setClubName("Dynamo");
        inputDto.setLeagueId(5L);

        when(clubService.create(inputDto)).thenReturn(clubDto);

        ResponseEntity<ClubDto> response = clubController.create(inputDto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(1L, response.getBody().getId());

        ArgumentCaptor<ClubDto> captor = ArgumentCaptor.forClass(ClubDto.class);
        verify(clubService).create(captor.capture());
        assertEquals("Dynamo", captor.getValue().getClubName());
    }


    @Test
    @DisplayName("update_validDto_returnsOkWithDto")
    void update_validDto_returnsOkWithDto() {
        // Arrange
        ClubDto updateDto = new ClubDto();
        updateDto.setClubName("Shakhtar");
        updateDto.setLeagueId(5L);

        ClubDto updatedDto = new ClubDto();
        updatedDto.setId(1L);
        updatedDto.setClubName("Shakhtar");
        updatedDto.setLeagueId(5L);

        when(clubService.update(1L, updateDto)).thenReturn(updatedDto);

        ResponseEntity<ClubDto> response = clubController.update(1L, updateDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Shakhtar", response.getBody().getClubName());
        verify(clubService).update(1L, updateDto);
    }


    @Test
    @DisplayName("delete_existingId_returnsNoContent")
    void delete_existingId_returnsNoContent() {
        doNothing().when(clubService).delete(1L);

        ResponseEntity<Void> response = clubController.delete(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(clubService).delete(1L);
    }

    @Test
    @DisplayName("delete_serviceThrowsNotFound_propagatesException")
    void delete_serviceThrowsNotFound_propagatesException() {
        doThrow(new EntityNotFoundException("Клуб з ID 123 не знайдений"))
                .when(clubService).delete(123L);

        assertThrows(EntityNotFoundException.class, () -> clubController.delete(123L));
    }
}
