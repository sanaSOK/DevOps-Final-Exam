package com.example.football_rental.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "terrain_images")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TerrainImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "terrain_id", nullable = false)
    private Long terrainId;

    @Column(name = "image_path", nullable = false)
    private String imagePath;

    @Column(name = "uploaded_at")
    private LocalDateTime uploadedAt;

    @PrePersist
    protected void onCreate() {
        uploadedAt = LocalDateTime.now();
    }
}
