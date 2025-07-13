package ru.practicum.shareit.item.dao;

import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface ItemDao {
    Item create(Item item);

    Item getOne(Long id);

    Item update(Item item);

    Collection<Item> getAllByOwner(Long userId);

    Collection<Item> getAllSearchedItems(String text);
}
