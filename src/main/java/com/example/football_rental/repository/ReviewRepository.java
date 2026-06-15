package com.example.football_rental.repository;

import com.example.football_rental.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findByTerrainId(Long terrainId);

    List<Review> findByUserId(Long userId);

    boolean existsByTerrainIdAndUserId(Long terrainId, Long userId);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.terrainId = :terrainId")
    Double findAverageRatingByTerrainId(Long terrainId);
}
