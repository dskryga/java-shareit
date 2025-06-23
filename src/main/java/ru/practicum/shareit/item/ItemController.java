package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.Collection;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    public ItemDto create(@RequestBody @Valid ItemDto itemDto, @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Получен запрос от пользователся с id {} на создание вещи {}", userId, itemDto.getName());
        return itemService.create(itemDto, userId);
    }

    @GetMapping("{id}")
    public ItemDto getOne(@PathVariable Long id) {
        log.info("Получен запрос на получение информации о вещи с id {}", id);
        return itemService.getOne(id);
    }

    @PatchMapping("{itemId}")
    public ItemDto update(@RequestBody ItemDto itemDto, @RequestHeader("X-Sharer-User-Id") Long userId,
                          @PathVariable Long itemId) {
        log.info("Получен запрос от пользователя с id {} на изменение вещи с id {}", userId, itemId);
        return itemService.update(itemDto, userId, itemId);
    }

    @GetMapping
    public Collection<ItemDto> getAllItemsByOwner(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Получен запрос от пользователя с id {} на получение информации о всех своих вещах", userId);
        return itemService.getAllByOwner(userId);
    }

    @GetMapping("/search")
    public Collection<ItemDto> getAllSearchedItems(@RequestParam String text) {
        log.info("Получен запрос на поиск всех вещей по ключевым словам: {}", text);
        return itemService.getAllSearchedItems(text);
    }
}
