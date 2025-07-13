package ru.practicum.shareit.booking.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoForItem;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.user.mapper.UserMapper;

@UtilityClass
public class BookingMapper {
    public Booking mapToBooking(BookingDto bookingDto) {
        Booking booking = new Booking();
        booking.setStartTime(bookingDto.getStart());
        booking.setEndTime(bookingDto.getEnd());
        return booking;
    }

    public BookingResponseDto mapToBookingResponseDto(Booking booking) {
        return BookingResponseDto.builder()
                .id(booking.getId())
                .start(booking.getStartTime())
                .end(booking.getEndTime())
                .status(booking.getStatus())
                .item(ItemMapper.mapToDto(booking.getItem()))
                .booker(UserMapper.mapToUserDto(booking.getBooker()))
                .build();
    }

    public BookingDtoForItem mapToBookingDtoForItem(Booking booking) {
        return BookingDtoForItem.builder()
                .id(booking.getId())
                .start(booking.getStartTime())
                .end(booking.getEndTime())
                .status(booking.getStatus())
                .itemId(booking.getItem().getId())
                .bookerId(booking.getBooker().getId())
                .build();
    }
}
