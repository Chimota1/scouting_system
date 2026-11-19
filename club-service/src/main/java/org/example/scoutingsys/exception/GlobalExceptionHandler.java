package org.example.scoutingsys.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ScoutLicenseExpiredException.class)
    public ResponseEntity<Map<String, Object>> handleLicenseExpired(ScoutLicenseExpiredException ex) {
        return ResponseEntity.status(HttpStatus.PAYMENT_REQUIRED).body(Map.of(
                "status", HttpStatus.PAYMENT_REQUIRED.value(),
                "error", "Scout license expired",
                "message", ex.getMessage(),
                "agency", ex.getAgency()
        ));
    }
}
