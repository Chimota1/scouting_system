package org.example.scoutingsys.player.aspect;

import org.example.scoutingsys.player.dto.PlayerDto;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

@Aspect
@Component
public class SortByNameAspect {

    @Around("@annotation(org.example.scoutingsys.player.aspect.SortByName)")
    public Object sort(ProceedingJoinPoint joinPoint) throws Throwable {
        Object result = joinPoint.proceed();
        if (result instanceof List<?> list) {
            return list.stream()
                    .sorted(Comparator.comparing(SortByNameAspect::nameOf,
                            String.CASE_INSENSITIVE_ORDER))
                    .toList();
        }
        return result;
    }

    private static String nameOf(Object value) {
        return value instanceof PlayerDto player && player.getPlayerName() != null
                ? player.getPlayerName()
                : "";
    }
}
