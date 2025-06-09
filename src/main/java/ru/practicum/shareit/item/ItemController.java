package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import java.util.Collection;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    public Item create(@RequestBody @Valid ItemDto itemDto, @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.create(itemDto, userId);
    }

    @GetMapping("{id}")
    public Item getOne(@PathVariable Long id) {
        return itemService.getOne(id);
    }

    @PatchMapping("{itemId}")
    public Item update(@RequestBody ItemDto itemDto, @RequestHeader("X-Sharer-User-Id") Long userId,
                       @PathVariable Long itemId) {
        return itemService.update(itemDto, userId, itemId);
    }

    @GetMapping
    public Collection<Item> getAllItemsByOwner(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.getAllByOwner(userId);
    }

    @GetMapping("/search")
    public Collection<Item> getAllSearchedItems(@RequestParam String text) {
        return itemService.getAllSearchedItems(text);
    }
}
