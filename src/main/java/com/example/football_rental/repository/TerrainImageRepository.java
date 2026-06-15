package com.example.football_rental.repository;

import com.example.football_rental.model.TerrainImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TerrainImageRepository extends JpaRepository<TerrainImage, Long> {

    List<TerrainImage> findByTerrainId(Long terrainId);

    void deleteByTerrainId(Long terrainId);
}
