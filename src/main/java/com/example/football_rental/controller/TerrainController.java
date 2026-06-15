package com.example.football_rental.controller;

import com.example.football_rental.model.Terrain;
import com.example.football_rental.repository.TerrainRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/terrains")
@RequiredArgsConstructor
public class TerrainController {

    private final TerrainRepository terrainRepository;

    /** GET /api/terrains — list all terrains */
    @GetMapping
    public ResponseEntity<List<Terrain>> getAllTerrains() {
        return ResponseEntity.ok(terrainRepository.findAll());
    }

    /** GET /api/terrains/available — list available terrains */
    @GetMapping("/available")
    public ResponseEntity<List<Terrain>> getAvailableTerrains() {
        return ResponseEntity.ok(terrainRepository.findByIsAvailableTrue());
    }

    /** GET /api/terrains/{id} — get a single terrain */
    @GetMapping("/{id}")
    public ResponseEntity<Terrain> getTerrainById(@PathVariable Long id) {
        return terrainRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /** GET /api/terrains/owner/{ownerId} — list terrains by owner */
    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<List<Terrain>> getTerrainsByOwner(@PathVariable Long ownerId) {
        return ResponseEntity.ok(terrainRepository.findByOwnerId(ownerId));
    }

    /** GET /api/terrains/search?q=... — search terrains by title */
    @GetMapping("/search")
    public ResponseEntity<List<Terrain>> searchTerrains(@RequestParam String q) {
        return ResponseEntity.ok(terrainRepository.findByTitleContainingIgnoreCase(q));
    }

    /** POST /api/terrains — create a new terrain */
    @PostMapping
    public ResponseEntity<Terrain> createTerrain(@Valid @RequestBody Terrain terrain) {
        Terrain saved = terrainRepository.save(terrain);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    /** PUT /api/terrains/{id} — update a terrain */
    @PutMapping("/{id}")
    public ResponseEntity<Terrain> updateTerrain(
            @PathVariable Long id,
            @Valid @RequestBody Terrain updatedTerrain) {
        return terrainRepository.findById(id)
                .map(existing -> {
                    updatedTerrain.setId(existing.getId());
                    return ResponseEntity.ok(terrainRepository.save(updatedTerrain));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /** DELETE /api/terrains/{id} — delete a terrain */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTerrain(@PathVariable Long id) {
        if (!terrainRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        terrainRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
