package org.example.scoutingsys.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.example.scoutingsys.dto.ClubDto;
import org.example.scoutingsys.dto.PlayerDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SortByNameAspectTest {

    @Mock
    private ProceedingJoinPoint joinPoint;

    @InjectMocks
    private SortByNameAspect aspect;

    private static ClubDto club(String name) {
        ClubDto dto = new ClubDto();
        dto.setClubName(name);
        return dto;
    }

    @Test
    @DisplayName("Список клубів з proceed() повертається відсортованим за назвою")
    void sortsClubsByName() throws Throwable {
        List<ClubDto> unsortedClubs = List.of(club("Shakhtar"), club("barcelona"), club("Arsenal"));
        when(joinPoint.proceed()).thenReturn(unsortedClubs);

        List<?> result = (List<?>) aspect.sort(joinPoint);

        assertEquals(List.of("Arsenal", "barcelona", "Shakhtar"),
                result.stream().map(c -> ((ClubDto) c).getClubName()).toList());
        verify(joinPoint, times(1)).proceed();
    }

    @Test
    @DisplayName("Працює і для гравців; запис без імені йде на початок, без NPE")
    void sortsPlayersWithNullName() throws Throwable {
        PlayerDto noName = new PlayerDto();
        PlayerDto mudryk = new PlayerDto();
        mudryk.setPlayerName("Mudryk");
        PlayerDto dovbyk = new PlayerDto();
        dovbyk.setPlayerName("Dovbyk");
        when(joinPoint.proceed()).thenReturn(List.of(mudryk, noName, dovbyk));

        List<?> result = (List<?>) aspect.sort(joinPoint);

        assertEquals(List.of(noName, dovbyk, mudryk), result);
        verify(joinPoint, times(1)).proceed();
    }

    @Test
    @DisplayName("Не список (один об'єкт) повертається без змін")
    void nonListResultUnchanged() throws Throwable {
        ClubDto singleClub = club("Dynamo");
        when(joinPoint.proceed()).thenReturn(singleClub);

        Object result = aspect.sort(joinPoint);

        assertSame(singleClub, result);
        verify(joinPoint, times(1)).proceed();
    }

    @Test
    @DisplayName("Виняток з proceed() прокидається далі")
    void propagatesException() throws Throwable {
        when(joinPoint.proceed()).thenThrow(new IllegalStateException("db error"));

        assertThrows(IllegalStateException.class, () -> aspect.sort(joinPoint));
        verify(joinPoint, times(1)).proceed();
    }
}
