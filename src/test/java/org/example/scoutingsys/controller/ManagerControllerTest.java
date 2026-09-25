package org.example.scoutingsys.controller;

import jakarta.persistence.EntityNotFoundException;
import org.example.scoutingsys.dto.ManagerDto;
import org.example.scoutingsys.service.ManagerService;
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
class ManagerControllerTest {

    @Mock
    private ManagerService managerService;

    @InjectMocks
    private ManagerController managerController;

    private ManagerDto managerDto;

    @BeforeEach
    void setUp() {
        managerDto = new ManagerDto();
        managerDto.setId(1L);
        managerDto.setManagerName("Coach");
        managerDto.setManagerAge(45);
        managerDto.setCountryId(3L);
    }

    @Test
    @DisplayName("getAll_managersExist_returnsOkWithList")
    void getAll_managersExist_returnsOkWithList() {
        when(managerService.getAll()).thenReturn(List.of(managerDto));

        ResponseEntity<List<ManagerDto>> response = managerController.getAll();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        verify(managerService).getAll();
    }

    @Test
    @DisplayName("getById_existingId_returnsOkWithDto")
    void getById_existingId_returnsOkWithDto() {
        when(managerService.getById(1L)).thenReturn(managerDto);

        ResponseEntity<ManagerDto> response = managerController.getById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Coach", response.getBody().getManagerName());
    }

    @Test
    @DisplayName("getById_serviceThrowsNotFound_propagatesException")
    void getById_serviceThrowsNotFound_propagatesException() {
        when(managerService.getById(2L)).thenThrow(new EntityNotFoundException("Менеджер з ID 2 не знайдений"));

        assertThrows(EntityNotFoundException.class, () -> managerController.getById(2L));
    }


    @Test
    @DisplayName("create_validDto_returnsCreatedWithDto")
    void create_validDto_returnsCreatedWithDto() {
        ManagerDto inputDto = new ManagerDto();
        inputDto.setManagerName("Coach");
        inputDto.setManagerAge(45);
        inputDto.setCountryId(3L);

        when(managerService.create(inputDto)).thenReturn(managerDto);

        ResponseEntity<ManagerDto> response = managerController.create(inputDto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(1L, response.getBody().getId());

        ArgumentCaptor<ManagerDto> captor = ArgumentCaptor.forClass(ManagerDto.class);
        verify(managerService).create(captor.capture());
        assertEquals("Coach", captor.getValue().getManagerName());
    }

    @Test
    @DisplayName("update_validDto_returnsOkWithDto")
    void update_validDto_returnsOkWithDto() {
        ManagerDto updateDto = new ManagerDto();
        updateDto.setManagerName("New Coach");
        updateDto.setManagerAge(50);
        updateDto.setCountryId(3L);

        ManagerDto updatedDto = new ManagerDto();
        updatedDto.setId(1L);
        updatedDto.setManagerName("New Coach");
        updatedDto.setManagerAge(50);

        when(managerService.update(1L, updateDto)).thenReturn(updatedDto);

        ResponseEntity<ManagerDto> response = managerController.update(1L, updateDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("New Coach", response.getBody().getManagerName());
        verify(managerService).update(1L, updateDto);
    }

    @Test
    @DisplayName("delete_existingId_returnsNoContent")
    void delete_existingId_returnsNoContent() {
        doNothing().when(managerService).delete(1L);

        ResponseEntity<Void> response = managerController.delete(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(managerService).delete(1L);
    }

    @Test
    @DisplayName("delete_serviceThrowsNotFound_propagatesException")
    void delete_serviceThrowsNotFound_propagatesException() {
        doThrow(new EntityNotFoundException("Менеджер з ID 66 не знайдений"))
                .when(managerService).delete(66L);

        assertThrows(EntityNotFoundException.class, () -> managerController.delete(66L));
    }
}
