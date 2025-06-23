package ru.practicum.shareit.item.dao;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.HashMap;
import java.util.stream.Collectors;

@Repository
public class ItemDaoInMemoryImpl implements ItemDao {
    HashMap<Long, Item> items = new HashMap<>();
    Long count = 0L;

    @Override
    public Item create(Item item) {
        item.setId(getNextId());
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item getOne(Long id) {
        if (items.containsKey(id)) {
            return items.get(id);
        }
        throw new NotFoundException(String.format("Вещь с id %d не найдена", id));
    }

    @Override
    public Item update(Item item) {
        return items.replace(item.getId(), item);
    }

    @Override
    public Collection<Item> getAllByOwner(Long userId) {
        return items.values().stream()
                .filter(item -> item.getOwner().getId().equals(userId))
                .collect(Collectors.toList());
    }

    @Override
    public Collection<Item> getAllSearchedItems(String text) {
        return items.values().stream()
                .filter(item -> item.getAvailable() != null)
                .filter(Item::getAvailable)
                .filter(item -> item.getName().toLowerCase().contains(text) ||
                        item.getDescription().toLowerCase().contains(text))
                .collect(Collectors.toList());
    }

    private Long getNextId() {
        return ++count;
    }

}
