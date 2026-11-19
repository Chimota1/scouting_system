package org.example.scoutingsys.aspect;

import org.example.scoutingsys.annotation.RequireActiveScoutLicense;
import org.example.scoutingsys.annotation.ScoutAgency;
import org.example.scoutingsys.exception.ScoutLicenseExpiredException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.aop.aspectj.annotation.AspectJProxyFactory;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ScoutLicenseAspectTest {

    static class ReportService {
        int calls = 0;

        @RequireActiveScoutLicense
        public String generateReport(@ScoutAgency String agency) {
            calls++;
            return "report for " + agency;
        }

        public String unprotected(@ScoutAgency String agency) {
            calls++;
            return "ok";
        }
    }

    @InjectMocks
    private ScoutLicenseAspect aspect;

    private ReportService target;
    private ReportService proxy;

    @BeforeEach
    void setUp() {
        target = new ReportService();
        AspectJProxyFactory factory = new AspectJProxyFactory(target);
        factory.setProxyTargetClass(true);
        factory.addAspect(aspect);
        proxy = factory.getProxy();
    }

    @Test
    @DisplayName("@Before: активна ліцензія — метод виконується")
    void activeLicense_methodIsCalled() {
        String validAgency = "Real-Madrid-Scout";

        String result = proxy.generateReport(validAgency);

        assertEquals("report for Real-Madrid-Scout", result);
        assertEquals(1, target.calls);
    }

    @Test
    @DisplayName("@Before: прострочена ліцензія — виняток, метод не виконується")
    void expiredLicense_throwsAndMethodIsNotCalled() {
        String expiredAgency = "EXPIRED-LICENSE";

        ScoutLicenseExpiredException ex = assertThrows(ScoutLicenseExpiredException.class,
                () -> proxy.generateReport(expiredAgency));
        assertEquals(expiredAgency, ex.getAgency());
        assertEquals(0, target.calls);
    }

    @Test
    @DisplayName("Методи без @RequireActiveScoutLicense не перевіряються")
    void methodWithoutAnnotation_isNotChecked() {
        String agency = "EXPIRED-LICENSE";

        String result = proxy.unprotected(agency);

        assertEquals("ok", result);
        assertEquals(1, target.calls);
    }

    @Test
    @DisplayName("Прямий виклик advice checkLicense(): валідна ліцензія проходить успішно")
    void directCheckLicense_validAgency_doesNotThrow() {
        String validAgency = "Independent-Scout";

        assertDoesNotThrow(() -> aspect.checkLicense(validAgency));
    }

    @Test
    @DisplayName("Прямий виклик advice checkLicense(): заблоковане агентство викидає ScoutLicenseExpiredException")
    void directCheckLicense_blockedAgency_throwsException() {
        String blockedAgency = "BLOCKED-AGENCY";

        ScoutLicenseExpiredException ex = assertThrows(ScoutLicenseExpiredException.class,
                () -> aspect.checkLicense(blockedAgency));
        assertEquals(blockedAgency, ex.getAgency());
    }
}
