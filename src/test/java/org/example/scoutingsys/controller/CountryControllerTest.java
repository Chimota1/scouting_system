package org.example.scoutingsys.controller;

import jakarta.persistence.EntityNotFoundException;
import org.example.scoutingsys.dto.CountryDto;
import org.example.scoutingsys.service.CountryService;
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
class CountryControllerTest {

    @Mock
    private CountryService countryService;

    @InjectMocks
    private CountryController countryController;

    private CountryDto countryDto;

    @BeforeEach
    void setUp() {
        countryDto = new CountryDto();
        countryDto.setId(1L);
        countryDto.setCountryName("Ukraine");
        countryDto.setContinent("Europe");
    }

    @Test
    @DisplayName("getAll_countriesExist_returnsOkWithList")
    void getAll_countriesExist_returnsOkWithList() {
        when(countryService.getAll()).thenReturn(List.of(countryDto));

        ResponseEntity<List<CountryDto>> response = countryController.getAll();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        verify(countryService).getAll();
    }


    @Test
    @DisplayName("getById_existingId_returnsOkWithDto")
    void getById_existingId_returnsOkWithDto() {
        when(countryService.getById(1L)).thenReturn(countryDto);

        ResponseEntity<CountryDto> response = countryController.getById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Ukraine", response.getBody().getCountryName());
    }

    @Test
    @DisplayName("getById_serviceThrowsNotFound_propagatesException")
    void getById_serviceThrowsNotFound_propagatesException() {
        when(countryService.getById(99L)).thenThrow(new EntityNotFoundException("Країна з ID 99 не знайдена"));

        assertThrows(EntityNotFoundException.class, () -> countryController.getById(99L));
    }


    @Test
    @DisplayName("create_validDto_returnsCreatedWithDto")
    void create_validDto_returnsCreatedWithDto() {
        CountryDto inputDto = new CountryDto();
        inputDto.setCountryName("Ukraine");
        inputDto.setContinent("Europe");

        when(countryService.create(inputDto)).thenReturn(countryDto);

        ResponseEntity<CountryDto> response = countryController.create(inputDto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(1L, response.getBody().getId());

        ArgumentCaptor<CountryDto> captor = ArgumentCaptor.forClass(CountryDto.class);
        verify(countryService).create(captor.capture());
        assertEquals("Ukraine", captor.getValue().getCountryName());
    }


    @Test
    @DisplayName("update_validDto_returnsOkWithDto")
    void update_validDto_returnsOkWithDto() {
        CountryDto updateDto = new CountryDto();
        updateDto.setCountryName("Poland");
        updateDto.setContinent("Europe");

        CountryDto updatedDto = new CountryDto();
        updatedDto.setId(1L);
        updatedDto.setCountryName("Poland");
        updatedDto.setContinent("Europe");

        when(countryService.update(1L, updateDto)).thenReturn(updatedDto);

        ResponseEntity<CountryDto> response = countryController.update(1L, updateDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Poland", response.getBody().getCountryName());
        verify(countryService).update(1L, updateDto);
    }


    @Test
    @DisplayName("delete_existingId_returnsNoContent")
    void delete_existingId_returnsNoContent() {
        doNothing().when(countryService).delete(1L);

        ResponseEntity<Void> response = countryController.delete(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(countryService).delete(1L);
    }

    @Test
    @DisplayName("delete_serviceThrowsNotFound_propagatesException")
    void delete_serviceThrowsNotFound_propagatesException() {
        doThrow(new EntityNotFoundException("Країна з ID 77 не знайдена"))
                .when(countryService).delete(77L);

        assertThrows(EntityNotFoundException.class, () -> countryController.delete(77L));
    }
}
