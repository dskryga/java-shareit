package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.Collection;


@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public BookingResponseDto create(@RequestBody @Valid BookingDto bookingDto,
                                     @RequestHeader(value = "X-Sharer-User-Id") Long userId) {
        log.info("Получен запрос на создание бронирования от пользователя с id {}", userId);
        return bookingService.create(bookingDto, userId);
    }

    @GetMapping("{bookingId}")
    public BookingResponseDto getBookingById(@PathVariable Long bookingId,
                                             @RequestHeader(value = "X-Sharer-User-Id") Long userId) {
        log.info("Получен запрос на вывод бронирования по id {} от пользователя с id {}", bookingId, userId);
        return bookingService.getBookingById(bookingId, userId);
    }

    @PatchMapping("{bookingId}")
    public BookingResponseDto approveBooking(@PathVariable Long bookingId,
                                             @RequestHeader(value = "X-Sharer-User-Id") Long userId,
                                             @RequestParam boolean approved) {
        log.info("Получен запрос на подтверждение/отклонение брони с id {} от пользователя с id {}", bookingId, userId);
        return bookingService.aprroveBooking(bookingId, userId, approved);
    }

    @GetMapping
    public Collection<BookingResponseDto> findByBookerAndState(@RequestHeader(value = "X-Sharer-User-Id") Long userId,
                                                               @RequestParam(name = "state", defaultValue = "ALL")
                                                               BookingState bookingState) {
        log.info("Получен запрос на поиск бронирования по букеру с id {} в статусе {}", userId, bookingState.name());
        return bookingService.findByBookerAndState(userId, bookingState);
    }

    @GetMapping("owner")
    public Collection<BookingResponseDto> findByOwnerAndState(@RequestHeader(value = "X-Sharer-User-Id") Long userId,
                                                              @RequestParam(name = "state", defaultValue = "ALL")
                                                              BookingState bookingState) {
        log.info("Получен запрос на поиск бронирования по владельцу вещи с id {} в статусе {}",
                userId, bookingState.name());
        return bookingService.findByOwnerAndState(userId, bookingState);
    }

}
