package com.example.football_rental.controller;

import com.example.football_rental.model.TerrainImage;
import com.example.football_rental.repository.TerrainImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/terrain-images")
@RequiredArgsConstructor
public class TerrainImageController {

    private final TerrainImageRepository terrainImageRepository;

    /** GET /api/terrain-images — list all images */
    @GetMapping
    public ResponseEntity<List<TerrainImage>> getAllImages() {
        return ResponseEntity.ok(terrainImageRepository.findAll());
    }

    /** GET /api/terrain-images/{id} — get a single image */
    @GetMapping("/{id}")
    public ResponseEntity<TerrainImage> getImageById(@PathVariable Long id) {
        return terrainImageRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /** GET /api/terrain-images/terrain/{terrainId} — get images by terrain */
    @GetMapping("/terrain/{terrainId}")
    public ResponseEntity<List<TerrainImage>> getImagesByTerrain(@PathVariable Long terrainId) {
        return ResponseEntity.ok(terrainImageRepository.findByTerrainId(terrainId));
    }

    /** POST /api/terrain-images — upload a new image record */
    @PostMapping
    public ResponseEntity<TerrainImage> createImage(@RequestBody TerrainImage image) {
        TerrainImage saved = terrainImageRepository.save(image);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    /** DELETE /api/terrain-images/{id} — delete a single image */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteImage(@PathVariable Long id) {
        if (!terrainImageRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        terrainImageRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    /** DELETE /api/terrain-images/terrain/{terrainId} — delete all images for a terrain */
    @Transactional
    @DeleteMapping("/terrain/{terrainId}")
    public ResponseEntity<Void> deleteImagesByTerrain(@PathVariable Long terrainId) {
        terrainImageRepository.deleteByTerrainId(terrainId);
        return ResponseEntity.noContent().build();
    }
}
