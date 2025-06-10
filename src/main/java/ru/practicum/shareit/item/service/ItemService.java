package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collection;

public interface ItemService {
    ItemDto create(ItemDto itemDto, Long userId);

    ItemDto getOne(Long id);

    ItemDto update(ItemDto itemDto, Long userId, Long itemId);

    Collection<ItemDto> getAllByOwner(Long userId);

    Collection<ItemDto> getAllSearchedItems(String text);
}
