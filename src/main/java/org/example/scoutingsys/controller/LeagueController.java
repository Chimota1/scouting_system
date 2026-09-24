package org.example.scoutingsys.controller;

import jakarta.validation.Valid;
import org.example.scoutingsys.dto.LeagueDto;
import org.example.scoutingsys.service.LeagueService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/leagues")
@CrossOrigin(origins = "*")
public class LeagueController {

    private final LeagueService service;

    public LeagueController(LeagueService service) {
        this.service = service;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('user', 'admin')")
    public ResponseEntity<List<LeagueDto>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('user', 'admin')")
    public ResponseEntity<LeagueDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<LeagueDto> create(
            @Valid @RequestBody LeagueDto LeagueDto) {

        return ResponseEntity
                .status(201)
                .body(service.create(LeagueDto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<LeagueDto> update(
            @PathVariable Long id,
            @Valid @RequestBody LeagueDto LeagueDto) {

        return ResponseEntity.ok(service.update(id, LeagueDto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
