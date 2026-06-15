package com.example.football_rental.controller;

import com.example.football_rental.model.Booking;
import com.example.football_rental.model.Booking.BookingStatus;
import com.example.football_rental.repository.BookingRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingRepository bookingRepository;

    /** GET /api/bookings — list all bookings */
    @GetMapping
    public ResponseEntity<List<Booking>> getAllBookings() {
        return ResponseEntity.ok(bookingRepository.findAll());
    }

    /** GET /api/bookings/{id} — get a single booking */
    @GetMapping("/{id}")
    public ResponseEntity<Booking> getBookingById(@PathVariable Long id) {
        return bookingRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /** GET /api/bookings/terrain/{terrainId} — get bookings for a terrain */
    @GetMapping("/terrain/{terrainId}")
    public ResponseEntity<List<Booking>> getBookingsByTerrain(@PathVariable Long terrainId) {
        return ResponseEntity.ok(bookingRepository.findByTerrainId(terrainId));
    }

    /** GET /api/bookings/renter/{renterId} — get bookings by renter */
    @GetMapping("/renter/{renterId}")
    public ResponseEntity<List<Booking>> getBookingsByRenter(@PathVariable Long renterId) {
        return ResponseEntity.ok(bookingRepository.findByRenterId(renterId));
    }

    /** GET /api/bookings/status/{status} — filter bookings by status */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Booking>> getBookingsByStatus(@PathVariable BookingStatus status) {
        return ResponseEntity.ok(bookingRepository.findByStatus(status));
    }

    /** POST /api/bookings — create a new booking */
    @PostMapping
    public ResponseEntity<Booking> createBooking(@Valid @RequestBody Booking booking) {
        booking.setStatus(BookingStatus.pending);
        Booking saved = bookingRepository.save(booking);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    /** PUT /api/bookings/{id} — update a booking */
    @PutMapping("/{id}")
    public ResponseEntity<Booking> updateBooking(
            @PathVariable Long id,
            @Valid @RequestBody Booking updatedBooking) {
        return bookingRepository.findById(id)
                .map(existing -> {
                    updatedBooking.setId(existing.getId());
                    return ResponseEntity.ok(bookingRepository.save(updatedBooking));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /** PATCH /api/bookings/{id}/status — update booking status only */
    @PatchMapping("/{id}/status")
    public ResponseEntity<Booking> updateBookingStatus(
            @PathVariable Long id,
            @RequestParam BookingStatus status) {
        return bookingRepository.findById(id)
                .map(existing -> {
                    existing.setStatus(status);
                    return ResponseEntity.ok(bookingRepository.save(existing));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /** DELETE /api/bookings/{id} — delete a booking */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBooking(@PathVariable Long id) {
        if (!bookingRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        bookingRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
