package com.example.football_rental.repository;

import com.example.football_rental.model.Terrain;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TerrainRepository extends JpaRepository<Terrain, Long> {

    List<Terrain> findByOwnerId(Long ownerId);

    List<Terrain> findByIsAvailableTrue();

    List<Terrain> findByLocationContainingIgnoreCase(String location);

    List<Terrain> findByTitleContainingIgnoreCase(String title);
}
