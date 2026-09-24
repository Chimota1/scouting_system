package org.example.scoutingsys.controller;

import jakarta.validation.Valid;
import org.example.scoutingsys.dto.ManagerDto;
import org.example.scoutingsys.service.ManagerService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/managers")
@CrossOrigin(origins = "*")
public class ManagerController {

    private final ManagerService service;

    public ManagerController(ManagerService service) {
        this.service = service;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('user', 'admin')")
    public ResponseEntity<List<ManagerDto>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('user', 'admin')")
    public ResponseEntity<ManagerDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<ManagerDto> create(
            @Valid @RequestBody ManagerDto ManagerDto) {

        return ResponseEntity
                .status(201)
                .body(service.create(ManagerDto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<ManagerDto> update(
            @PathVariable Long id,
            @Valid @RequestBody ManagerDto ManagerDto) {

        return ResponseEntity.ok(service.update(id, ManagerDto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
