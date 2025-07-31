package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;

import java.util.Collection;


public interface BookingService {
    BookingResponseDto create(BookingDto bookingDto, Long userId);

    BookingResponseDto getBookingById(Long bookingId, Long userId);

    BookingResponseDto aprroveBooking(Long bookingId, Long userId, boolean approved);

    Collection<BookingResponseDto> findByBookerAndState(Long userId, BookingState bookingState);

    Collection<BookingResponseDto> findByOwnerAndState(Long userId, BookingState bookingState);
}
