package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBookings;

import java.util.Collection;

public interface ItemService {
    ItemDto create(ItemDto itemDto, Long userId);

    ItemDtoWithBookings getOne(Long id);

    ItemDto update(ItemDto itemDto, Long userId, Long itemId);

    Collection<ItemDtoWithBookings> getAllByOwner(Long userId);

    Collection<ItemDto> getAllSearchedItems(String text);

    CommentResponseDto createComment(Long userId, Long itemId, CommentRequestDto commentRequestDto);
}
