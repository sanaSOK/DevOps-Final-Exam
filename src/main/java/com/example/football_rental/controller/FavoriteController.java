package com.example.football_rental.controller;

import com.example.football_rental.model.Favorite;
import com.example.football_rental.repository.FavoriteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/favorites")
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteRepository favoriteRepository;

    /** GET /api/favorites — list all favorites */
    @GetMapping
    public ResponseEntity<List<Favorite>> getAllFavorites() {
        return ResponseEntity.ok(favoriteRepository.findAll());
    }

    /** GET /api/favorites/{id} — get a single favorite */
    @GetMapping("/{id}")
    public ResponseEntity<Favorite> getFavoriteById(@PathVariable Long id) {
        return favoriteRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /** GET /api/favorites/user/{userId} — get all favorites for a user */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Favorite>> getFavoritesByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(favoriteRepository.findByUserId(userId));
    }

    /** GET /api/favorites/terrain/{terrainId} — get all users who favorited a terrain */
    @GetMapping("/terrain/{terrainId}")
    public ResponseEntity<List<Favorite>> getFavoritesByTerrain(@PathVariable Long terrainId) {
        return ResponseEntity.ok(favoriteRepository.findByTerrainId(terrainId));
    }

    /** GET /api/favorites/check?userId=&terrainId= — check if a terrain is favorited */
    @GetMapping("/check")
    public ResponseEntity<Map<String, Object>> checkFavorite(
            @RequestParam Long userId,
            @RequestParam Long terrainId) {
        boolean exists = favoriteRepository.existsByUserIdAndTerrainId(userId, terrainId);
        return ResponseEntity.ok(Map.of(
                "userId", userId,
                "terrainId", terrainId,
                "isFavorited", exists
        ));
    }

    /** POST /api/favorites — add a terrain to favorites */
    @PostMapping
    public ResponseEntity<Favorite> addFavorite(@RequestBody Favorite favorite) {
        if (favoriteRepository.existsByUserIdAndTerrainId(
                favorite.getUserId(), favorite.getTerrainId())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        Favorite saved = favoriteRepository.save(favorite);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    /** DELETE /api/favorites/{id} — remove a favorite by ID */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFavorite(@PathVariable Long id) {
        if (!favoriteRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        favoriteRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    /** DELETE /api/favorites?userId=&terrainId= — remove favorite by user+terrain */
    @Transactional
    @DeleteMapping
    public ResponseEntity<Void> removeFavorite(
            @RequestParam Long userId,
            @RequestParam Long terrainId) {
        if (!favoriteRepository.existsByUserIdAndTerrainId(userId, terrainId)) {
            return ResponseEntity.notFound().build();
        }
        favoriteRepository.deleteByUserIdAndTerrainId(userId, terrainId);
        return ResponseEntity.noContent().build();
    }
}
