package org.example.scoutingsys.controller;

import jakarta.validation.Valid;
import org.example.scoutingsys.dto.ClubDto;
import org.example.scoutingsys.service.ClubService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clubs")
@CrossOrigin(origins = "*")
public class ClubController {

    private final ClubService service;

    public ClubController(ClubService service) {
        this.service = service;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('user', 'admin')")
    public ResponseEntity<List<ClubDto>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('user', 'admin')")
    public ResponseEntity<ClubDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<ClubDto> create(
            @Valid @RequestBody ClubDto clubDto) {

        return ResponseEntity
                .status(201)
                .body(service.create(clubDto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<ClubDto> update(
            @PathVariable Long id,
            @Valid @RequestBody ClubDto clubDto) {

        return ResponseEntity.ok(service.update(id, clubDto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
