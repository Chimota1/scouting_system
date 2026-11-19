package org.example.scoutingsys.player.aspect;

import org.aspectj.lang.JoinPoint;
import org.example.scoutingsys.player.aspect.AuditChange;
import org.example.scoutingsys.player.mapper.PlayerMapper;
import org.example.scoutingsys.player.model.Player;
import org.example.scoutingsys.player.repository.ClubReferenceRepository;
import org.example.scoutingsys.player.repository.CountryReferenceRepository;
import org.example.scoutingsys.player.repository.PlayerRepository;
import org.example.scoutingsys.player.service.PlayerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.aop.aspectj.annotation.AspectJProxyFactory;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ExtendWith(OutputCaptureExtension.class)
class AuditChangeAspectTest {

    @Mock
    private PlayerRepository playerRepository;

    @Mock
    private PlayerMapper playerMapper;

    @Mock
    private ClubReferenceRepository clubRepository;

    @Mock
    private CountryReferenceRepository countryRepository;

    @Mock
    private JoinPoint joinPoint;

    @Mock
    private AuditChange auditChange;

    @InjectMocks
    private AuditChangeAspect auditChangeAspect;

    private PlayerService proxy;

    @BeforeEach
    void setUp() {
        PlayerService target = new PlayerService(playerRepository, playerMapper, clubRepository, countryRepository);
        AspectJProxyFactory factory = new AspectJProxyFactory(target);
        factory.setProxyTargetClass(true);
        factory.addAspect(auditChangeAspect);
        proxy = factory.getProxy();
    }

    @Test
    @DisplayName("Зовнішній виклик через проксі: запис аудиту з'являється")
    void externalCall_writesAuditLog(CapturedOutput output) {
        Long playerId = 5L;
        when(playerRepository.existsById(playerId)).thenReturn(true);

        proxy.delete(playerId);

        verify(playerRepository).deleteById(playerId);
        assertTrue(output.getOut().contains("AUDIT: користувач 'anonymous' виконав DELETE над Player id=5"));
    }

    @Test
    @DisplayName("Метод завершився винятком: @AfterReturning не спрацьовує")
    void failedCall_noAuditLog(CapturedOutput output) {
        Long nonExistentId = 99L;
        when(playerRepository.existsById(nonExistentId)).thenReturn(false);

        assertThrows(RuntimeException.class, () -> proxy.delete(nonExistentId));
        assertFalse(output.getOut().contains("AUDIT"));
    }

    @Test
    @DisplayName("Self-invocation: this.delete() видаляє гравців, але аудит мовчки НЕ спрацьовує")
    void selfInvocation_noAuditLog(CapturedOutput output) {
        Long clubId = 10L;
        Player player1 = new Player();
        player1.setId(1L);
        Player player2 = new Player();
        player2.setId(2L);
        when(playerRepository.findAllByClubId(clubId)).thenReturn(List.of(player1, player2));
        when(playerRepository.existsById(anyLong())).thenReturn(true);

        proxy.deleteAllByClub(clubId);

        verify(playerRepository).deleteById(1L);
        verify(playerRepository).deleteById(2L);
        assertFalse(output.getOut().contains("AUDIT"));
    }

    @Test
    @DisplayName("Прямий виклик advice через mock JoinPoint без контексту Spring")
    void directAdviceCall_withMockJoinPoint(CapturedOutput output) {
        when(auditChange.action()).thenReturn("UPDATE");
        when(auditChange.entity()).thenReturn("Club");
        when(joinPoint.getArgs()).thenReturn(new Object[]{42L});

        auditChangeAspect.audit(joinPoint, auditChange);

        assertTrue(output.getOut().contains("AUDIT: користувач 'anonymous' виконав UPDATE над Club id=42"));
    }
}
