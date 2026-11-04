package org.example.scoutingsys.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.example.scoutingsys.dto.ClubDto;
import org.example.scoutingsys.dto.CountryDto;
import org.example.scoutingsys.dto.LeagueDto;
import org.example.scoutingsys.dto.ManagerDto;
import org.example.scoutingsys.dto.PlayerDto;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

@Aspect
@Component
public class SortByNameAspect {

    @Around("@annotation(org.example.scoutingsys.annotation.SortByName)")
    public Object sort(ProceedingJoinPoint joinPoint) throws Throwable {
        Object result = joinPoint.proceed();

        if (result instanceof List<?> list) {
            return list.stream()
                    .sorted(Comparator.comparing(SortByNameAspect::nameOf, String.CASE_INSENSITIVE_ORDER))
                    .toList();
        }
        return result;
    }

    private static String nameOf(Object dto) {
        String name = null;
        if (dto instanceof CountryDto country) name = country.getCountryName();
        if (dto instanceof LeagueDto league) name = league.getLeagueName();
        if (dto instanceof ClubDto club) name = club.getClubName();
        if (dto instanceof ManagerDto manager) name = manager.getManagerName();
        if (dto instanceof PlayerDto player) name = player.getPlayerName();
        return name != null ? name : "";
    }
}
