package org.example.scoutingsys.player.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.example.scoutingsys.player.dto.PlayerDto;
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

    @Test
    void sortsPlayersWithNullName() throws Throwable {
        PlayerDto noName = new PlayerDto();
        PlayerDto mudryk = new PlayerDto();
        mudryk.setPlayerName("Mudryk");
        PlayerDto dovbyk = new PlayerDto();
        dovbyk.setPlayerName("Dovbyk");
        when(joinPoint.proceed()).thenReturn(List.of(mudryk, noName, dovbyk));

        assertEquals(List.of(noName, dovbyk, mudryk),
                aspect.sort(joinPoint));
    }

    @Test
    void nonListResultIsUnchanged() throws Throwable {
        PlayerDto player = new PlayerDto();
        when(joinPoint.proceed()).thenReturn(player);

        assertSame(player, aspect.sort(joinPoint));
    }
}
