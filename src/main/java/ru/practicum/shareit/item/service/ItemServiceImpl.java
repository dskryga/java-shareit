package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.AccessDeniedException;
import ru.practicum.shareit.item.dao.ItemDao;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserDao;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {

    private final ItemDao itemDao;
    private final UserDao userDao;

    @Override
    public ItemDto create(ItemDto itemDto, Long userId) {
        Item itemToCreate = ItemMapper.mapToItem(itemDto);
        itemToCreate.setOwner(userDao.getOne(userId));
        return ItemMapper.mapToDto(itemDao.create(itemToCreate));
    }

    @Override
    public ItemDto getOne(Long id) {
        return ItemMapper.mapToDto(itemDao.getOne(id));
    }

    @Override
    public ItemDto update(ItemDto itemDto, Long userId, Long itemId) {
        Item itemToUpdate = itemDao.getOne(itemId);
        if (itemToUpdate.getOwner().getId().equals(userId)) {
            if (itemDto.getName() != null) {
                itemToUpdate.setName(itemDto.getName());
            }
            if (itemDto.getDescription() != null) {
                itemToUpdate.setDescription(itemDto.getDescription());
            }
            if (itemDto.getAvailable() != itemToUpdate.getAvailable()) {
                itemToUpdate.setAvailable(itemDto.getAvailable());
            }
            return ItemMapper.mapToDto(itemDao.update(itemToUpdate));
        }
        throw new AccessDeniedException(String.format("Доступ на редактирование закрыт:" +
                "Пользователь с id %d не является владельцем вещи с id %d", userId, itemId));
    }

    @Override
    public Collection<ItemDto> getAllByOwner(Long userId) {
        userDao.getOne(userId);
        return itemDao.getAllByOwner(userId).stream()
                .map(ItemMapper::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<ItemDto> getAllSearchedItems(String text) {
        if (text.isBlank()) return List.of();
        log.info("Мы здесь, текст {}", text);
        text = text.trim().toLowerCase();
        return itemDao.getAllSearchedItems(text).stream()
                .map(ItemMapper::mapToDto)
                .collect(Collectors.toList());
    }
}
