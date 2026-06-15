package com.example.football_rental.repository;

import com.example.football_rental.model.Booking;
import com.example.football_rental.model.Booking.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByTerrainId(Long terrainId);

    List<Booking> findByRenterId(Long renterId);

    List<Booking> findByStatus(BookingStatus status);

    List<Booking> findByTerrainIdAndStatus(Long terrainId, BookingStatus status);

    List<Booking> findByRenterIdAndStatus(Long renterId, BookingStatus status);
}
