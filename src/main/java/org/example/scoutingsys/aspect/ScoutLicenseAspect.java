package org.example.scoutingsys.aspect;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.example.scoutingsys.exception.ScoutLicenseExpiredException;
import org.springframework.stereotype.Component;

import java.util.Set;

@Aspect
@Component
public class ScoutLicenseAspect {

    private static final Set<String> EXPIRED_AGENCIES = Set.of("EXPIRED-LICENSE", "BLOCKED-AGENCY");

    @Before("@annotation(org.example.scoutingsys.annotation.RequireActiveScoutLicense) && args(agency)")
    public void checkLicense(String agency) {
        if (EXPIRED_AGENCIES.contains(agency)) {
            throw new ScoutLicenseExpiredException(agency);
        }
    }
}
