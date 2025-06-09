package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface ItemService {
    Item create(ItemDto itemDto, Long userId);

    Item getOne(Long id);

    Item update(ItemDto itemDto, Long userId, Long itemId);

    Collection<Item> getAllByOwner(Long userId);

    Collection<Item> getAllSearchedItems(String text);
}
