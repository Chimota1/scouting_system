package org.example.scoutingsys.player.controller;

import jakarta.validation.Valid;
import org.example.scoutingsys.player.dto.PlayerDto;
import org.example.scoutingsys.player.service.PlayerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/players")
@CrossOrigin(origins = "*")
public class PlayerController {

    private final PlayerService service;

    public PlayerController(PlayerService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<PlayerDto>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/scout-report")
    public ResponseEntity<java.util.Map<String, Object>> getScoutReport(
            @RequestParam(defaultValue = "unknown") String agency) {
        return ResponseEntity.ok(java.util.Map.of(
                "agency", agency,
                "totalPlayers", service.getAll().size()
        ));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlayerDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PostMapping
    public ResponseEntity<PlayerDto> create(
            @Valid @RequestBody PlayerDto playerDto) {

        return ResponseEntity
                .status(201)
                .body(service.create(playerDto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PlayerDto> update(
            @PathVariable Long id,
            @Valid @RequestBody PlayerDto playerDto) {

        return ResponseEntity.ok(service.update(id, playerDto));
    }

    @DeleteMapping("/club/{clubId}")
    public ResponseEntity<Void> deleteAllByClub(@PathVariable Long clubId) {
        service.deleteAllByClub(clubId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
