package org.example.scoutingsys.controller;

import org.example.scoutingsys.dto.ClubDossierDto;
import org.example.scoutingsys.service.ClubAnalyticsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/clubs")
public class ClubAnalyticsController {

    private final ClubAnalyticsService clubAnalyticsService;

    public ClubAnalyticsController(ClubAnalyticsService clubAnalyticsService) {
        this.clubAnalyticsService = clubAnalyticsService;
    }

    @GetMapping("/{id}/dossier")
    public ResponseEntity<ClubDossierDto> getClubDossier(@PathVariable Long id) {
        ClubDossierDto dossier = clubAnalyticsService.generateClubDossier(id).join();
        return ResponseEntity.ok(dossier);
    }

    @PostMapping("/{id}/notify-board")
    public ResponseEntity<String> notifyBoard(@PathVariable Long id,
                                              @RequestParam(defaultValue = "board@club.com") String email) {
        clubAnalyticsService.notifyBoard(id, email);
        return ResponseEntity.accepted().body("Board notification scheduled");
    }

    @GetMapping("/limited-audit/{id}")
    public ResponseEntity<String> runAudit(@PathVariable Long id,
                                           @RequestParam(defaultValue = "300") long sleepMillis) {
        String result = clubAnalyticsService.runAuditOnLimitedPool(id, sleepMillis);
        return ResponseEntity.ok(result);
    }
}
