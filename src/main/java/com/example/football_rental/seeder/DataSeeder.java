package com.example.football_rental.seeder;

import com.example.football_rental.model.*;
import com.example.football_rental.model.Booking.BookingStatus;
import com.example.football_rental.model.Payment.PaymentStatus;
import com.example.football_rental.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DataSeeder seeds the database with sample data on application startup.
 * Only runs when the database tables are empty to avoid duplicate data.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final TerrainRepository terrainRepository;
    private final TerrainImageRepository terrainImageRepository;
    private final BookingRepository bookingRepository;
    private final PaymentRepository paymentRepository;
    private final ReviewRepository reviewRepository;
    private final FavoriteRepository favoriteRepository;

    @Override
    public void run(String... args) {
        if (terrainRepository.count() > 0) {
            log.info("Database already seeded. Skipping...");
            return;
        }

        log.info("Seeding database with sample data...");

        // ─────────────────────────────────────────────
        // Seed Terrains (owner_id references a user ID)
        // ─────────────────────────────────────────────
        Terrain terrain1 = terrainRepository.save(Terrain.builder()
                .ownerId(1L)
                .title("Green Valley Football Field")
                .description("A well-maintained football field with natural grass and floodlights.")
                .location("Phnom Penh, Cambodia")
                .areaSize(new BigDecimal("5000.00"))
                .pricePerDay(new BigDecimal("150.00"))
                .availableFrom(LocalDateTime.now())
                .availableTo(LocalDateTime.now().plusYears(1))
                .isAvailable(true)
                .build());

        Terrain terrain2 = terrainRepository.save(Terrain.builder()
                .ownerId(2L)
                .title("Sunrise Sports Complex")
                .description("Indoor and outdoor football facilities with changing rooms.")
                .location("Siem Reap, Cambodia")
                .areaSize(new BigDecimal("8000.00"))
                .pricePerDay(new BigDecimal("250.00"))
                .availableFrom(LocalDateTime.now())
                .availableTo(LocalDateTime.now().plusMonths(6))
                .isAvailable(true)
                .build());

        Terrain terrain3 = terrainRepository.save(Terrain.builder()
                .ownerId(1L)
                .title("City Center Mini Pitch")
                .description("Compact 5-a-side pitch in the heart of the city.")
                .location("Battambang, Cambodia")
                .areaSize(new BigDecimal("2000.00"))
                .pricePerDay(new BigDecimal("80.00"))
                .isAvailable(true)
                .build());

        log.info("Seeded {} terrains", terrainRepository.count());

        // ─────────────────────────────────────────────
        // Seed TerrainImages
        // ─────────────────────────────────────────────
        TerrainImage image1 = terrainImageRepository.save(TerrainImage.builder()
                .terrainId(terrain1.getId())
                .imagePath("/images/terrain1_main.jpg")
                .build());

        terrainImageRepository.save(TerrainImage.builder()
                .terrainId(terrain1.getId())
                .imagePath("/images/terrain1_side.jpg")
                .build());

        TerrainImage image2 = terrainImageRepository.save(TerrainImage.builder()
                .terrainId(terrain2.getId())
                .imagePath("/images/terrain2_main.jpg")
                .build());

        terrainImageRepository.save(TerrainImage.builder()
                .terrainId(terrain3.getId())
                .imagePath("/images/terrain3_main.jpg")
                .build());

        // Update main_image_id for terrains
        terrain1.setMainImageId(image1.getId());
        terrainRepository.save(terrain1);

        terrain2.setMainImageId(image2.getId());
        terrainRepository.save(terrain2);

        log.info("Seeded {} terrain images", terrainImageRepository.count());

        // ─────────────────────────────────────────────
        // Seed Bookings (renter_id references user IDs)
        // ─────────────────────────────────────────────
        Booking booking1 = bookingRepository.save(Booking.builder()
                .terrainId(terrain1.getId())
                .renterId(3L)
                .startDate(LocalDate.now().plusDays(5))
                .endDate(LocalDate.now().plusDays(7))
                .totalPrice(new BigDecimal("300.00"))
                .status(BookingStatus.approved)
                .build());

        Booking booking2 = bookingRepository.save(Booking.builder()
                .terrainId(terrain2.getId())
                .renterId(4L)
                .startDate(LocalDate.now().plusDays(10))
                .endDate(LocalDate.now().plusDays(12))
                .totalPrice(new BigDecimal("500.00"))
                .status(BookingStatus.pending)
                .build());

        Booking booking3 = bookingRepository.save(Booking.builder()
                .terrainId(terrain3.getId())
                .renterId(3L)
                .startDate(LocalDate.now().minusDays(10))
                .endDate(LocalDate.now().minusDays(8))
                .totalPrice(new BigDecimal("160.00"))
                .status(BookingStatus.completed)
                .build());

        log.info("Seeded {} bookings", bookingRepository.count());

        // ─────────────────────────────────────────────
        // Seed Payments
        // ─────────────────────────────────────────────
        paymentRepository.save(Payment.builder()
                .bookingId(booking1.getId())
                .paymentMethod("credit_card")
                .amountPaid(new BigDecimal("300.00"))
                .paymentDate(LocalDateTime.now().minusDays(1))
                .status(PaymentStatus.paid)
                .transactionId("TXN-001-ABC123")
                .build());

        paymentRepository.save(Payment.builder()
                .bookingId(booking3.getId())
                .paymentMethod("bank_transfer")
                .amountPaid(new BigDecimal("160.00"))
                .paymentDate(LocalDateTime.now().minusDays(12))
                .status(PaymentStatus.paid)
                .transactionId("TXN-002-DEF456")
                .build());

        log.info("Seeded {} payments", paymentRepository.count());

        // ─────────────────────────────────────────────
        // Seed Reviews
        // ─────────────────────────────────────────────
        reviewRepository.save(Review.builder()
                .terrainId(terrain1.getId())
                .userId(3L)
                .rating(5)
                .comment("Excellent field! Well-maintained grass and great facilities.")
                .build());

        reviewRepository.save(Review.builder()
                .terrainId(terrain3.getId())
                .userId(3L)
                .rating(4)
                .comment("Good pitch for small games. A bit small but clean.")
                .build());

        reviewRepository.save(Review.builder()
                .terrainId(terrain2.getId())
                .userId(4L)
                .rating(3)
                .comment("Average experience. The changing rooms could be cleaner.")
                .build());

        log.info("Seeded {} reviews", reviewRepository.count());

        // ─────────────────────────────────────────────
        // Seed Favorites
        // ─────────────────────────────────────────────
        favoriteRepository.save(Favorite.builder()
                .userId(3L)
                .terrainId(terrain1.getId())
                .build());

        favoriteRepository.save(Favorite.builder()
                .userId(3L)
                .terrainId(terrain2.getId())
                .build());

        favoriteRepository.save(Favorite.builder()
                .userId(4L)
                .terrainId(terrain1.getId())
                .build());

        log.info("Seeded {} favorites", favoriteRepository.count());
        log.info("✅ Database seeding complete!");
    }
}
