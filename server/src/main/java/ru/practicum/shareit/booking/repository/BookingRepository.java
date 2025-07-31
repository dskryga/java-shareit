package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    @Query("SELECT b FROM Booking b " +
            "WHERE b.booker.id = :userId " +
            "AND (:state = 'ALL' " +
            "OR (:state = 'CURRENT' AND b.startTime <= CURRENT_TIMESTAMP AND b.endTime >= CURRENT_TIMESTAMP) " +
            "OR (:state = 'PAST' AND b.endTime < CURRENT_TIMESTAMP) " +
            "OR (:state = 'FUTURE' AND b.startTime > CURRENT_TIMESTAMP) " +
            "OR (:state = 'WAITING' AND b.status = 'WAITING') " +
            "OR (:state = 'REJECTED' AND b.status = 'REJECTED')) " +
            "ORDER BY b.startTime DESC")
    Collection<Booking> findByBookerAndState(@Param("userId") Long userId, @Param("state") String state);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.ownerId = :userId " +
            "AND (:state = 'ALL' " +
            "OR (:state = 'CURRENT' AND b.startTime <= CURRENT_TIMESTAMP AND b.endTime >= CURRENT_TIMESTAMP) " +
            "OR (:state = 'PAST' AND b.endTime < CURRENT_TIMESTAMP) " +
            "OR (:state = 'FUTURE' AND b.startTime > CURRENT_TIMESTAMP) " +
            "OR (:state = 'WAITING' AND b.status = 'WAITING') " +
            "OR (:state = 'REJECTED' AND b.status = 'REJECTED')) " +
            "ORDER BY b.startTime DESC")
    Collection<Booking> findByOwnerAndState(@Param("userId") Long userId, @Param("state") String state);

    Optional<Booking> findTopByItemIdAndEndTimeBeforeAndStatusOrderByEndTimeDesc(
            Long itemId,
            LocalDateTime endTimeBefore,
            BookingStatus bookingStatus);

    Optional<Booking> findTopByItemIdAndEndTimeAfterAndStatusOrderByEndTimeAsc(
            Long itemId,
            LocalDateTime endTimeBefore,
            BookingStatus bookingStatus
    );

    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.id = :itemId " +
            "AND b.booker.id = :userId " +
            "AND b.status = 'APPROVED' " +
            "AND b.endTime < CURRENT_TIMESTAMP " +
            "ORDER BY b.endTime DESC")
    Collection<Booking> findCompletedApprovedBookingsForUserAndItem(
            @Param("itemId") Long itemId,
            @Param("userId") Long userId);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.id IN :itemIds " +
            "AND b.status = 'APPROVED' " +
            "ORDER BY b.startTime")
    Collection<Booking> findApprovedBookingsForItems(@Param("itemIds") List<Long> itemIds);
}
