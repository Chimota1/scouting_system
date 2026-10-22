package org.example.scoutingsys.controller;

import jakarta.validation.Valid;
import org.example.scoutingsys.annotation.PublicGetMapping;
import org.example.scoutingsys.annotation.ScoutAgency;
import org.example.scoutingsys.dto.PlayerDto;
import org.example.scoutingsys.service.PlayerService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/players")
@CrossOrigin(origins = "*")
public class PlayerController {

    private final PlayerService service;

    public PlayerController(PlayerService service) {
        this.service = service;
    }

    @PublicGetMapping
    @PreAuthorize("hasAnyRole('user', 'admin')")
    public ResponseEntity<List<PlayerDto>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @PublicGetMapping("/scout-report")
    public ResponseEntity<Map<String, Object>> getScoutReport(@ScoutAgency String agency) {
        return ResponseEntity.ok(Map.of(
                "agency", agency,
                "totalPlayers", service.getAll().size()
        ));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('user', 'admin')")
    public ResponseEntity<PlayerDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<PlayerDto> create(
            @Valid @RequestBody PlayerDto playerDto) {

        return ResponseEntity
                .status(201)
                .body(service.create(playerDto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<PlayerDto> update(
            @PathVariable Long id,
            @Valid @RequestBody PlayerDto playerDto) {

        return ResponseEntity.ok(service.update(id, playerDto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
