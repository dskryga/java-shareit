package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;


@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> createBooking(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestBody @Valid BookingDto bookingDto) {
        log.info("Gateway: Создание бронирования от пользователя с ID {}", userId);
        return bookingClient.bookItem(userId, bookingDto);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBookingById(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @PathVariable long bookingId) {
        log.info("Gateway: Получение бронирования с ID {} для пользователя с ID {}", bookingId, userId);
        return bookingClient.getBooking(userId, bookingId);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approveBooking(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @PathVariable long bookingId,
            @RequestParam boolean approved) {
        log.info("Gateway: Подтверждение/отклонение бронирования {} пользователем {}", bookingId, userId);
        return bookingClient.approveBooking(bookingId, userId, approved);
    }

    @GetMapping
    public ResponseEntity<Object> findByBookerAndState(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestParam(name = "state", defaultValue = "ALL") String state) {
        log.info("Gateway: Поиск бронирований пользователя {} со статусом {}", userId, state);
        return bookingClient.getBookings(userId, state, null, null);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> findByOwnerAndState(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestParam(name = "state", defaultValue = "ALL") String state) {
        log.info("Gateway: Поиск бронирований владельца {} со статусом {}", userId, state);
        return bookingClient.getOwnerBookings(userId, state, null, null);
    }
}