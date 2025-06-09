package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.AccessDeniedException;
import ru.practicum.shareit.item.dao.ItemDao;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserDao;

import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemDao itemDao;
    private final UserDao userDao;

    @Override
    public Item create(ItemDto itemDto, Long userId) {
        Item itemToCreate = ItemMapper.mapToItem(itemDto);
        itemToCreate.setOwner(userDao.getOne(userId));
        return itemDao.create(itemToCreate);
    }

    @Override
    public Item getOne(Long id) {
        return itemDao.getOne(id);
    }

    @Override
    public Item update(ItemDto itemDto, Long userId, Long itemId) {
        if (itemDao.getOne(itemId).getOwner().getId().equals(userId)) {
            Item itemToUpdate = itemDao.getOne(itemId);
            if (itemDto.getName() != null) {
                itemToUpdate.setName(itemDto.getName());
            }
            if (itemDto.getDescription() != null) {
                itemToUpdate.setDescription(itemDto.getDescription());
            }
            if (itemDto.getAvailable() != itemToUpdate.getAvailable()) {
                itemToUpdate.setAvailable(itemDto.getAvailable());
            }
            return itemDao.update(itemToUpdate);
        }
        throw new AccessDeniedException(String.format("Доступ на редактирование закрыт:" +
                "Пользователь с id %d не является владельцем вещи с id %d", userId, itemId));
    }

    @Override
    public Collection<Item> getAllByOwner(Long userId) {
        userDao.getOne(userId);
        return itemDao.getAllByOwner(userId);
    }

    @Override
    public Collection<Item> getAllSearchedItems(String text) {
        text = text.trim().toLowerCase();
        if (text.isBlank()) return List.of();
        return itemDao.getAllSearchedItems(text);
    }
}
