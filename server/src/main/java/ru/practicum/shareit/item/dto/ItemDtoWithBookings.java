package ru.practicum.shareit.item.dto;

import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingDtoForItem;

import java.util.Collection;

@Data
public class ItemDtoWithBookings {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private BookingDtoForItem lastBooking;
    private BookingDtoForItem nextBooking;
    private Collection<CommentResponseDto> comments;
}
