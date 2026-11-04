package org.example.scoutingsys.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.example.scoutingsys.annotation.AuditChange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class AuditChangeAspect {

    private static final Logger log = LoggerFactory.getLogger(AuditChangeAspect.class);

    @AfterReturning("@annotation(auditChange)")
    public void audit(JoinPoint joinPoint, AuditChange auditChange) {
        Object[] args = joinPoint.getArgs();
        String id = (args.length > 0 && args[0] instanceof Long) ? " id=" + args[0] : "";

        log.info("AUDIT: користувач '{}' виконав {} над {}{}",
                currentUser(), auditChange.action(), auditChange.entity(), id);
    }

    private String currentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null ? auth.getName() : "anonymous";
    }
}
