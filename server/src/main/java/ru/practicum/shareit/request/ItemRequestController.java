package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    @PostMapping
    ItemRequestResponseDto create(@RequestBody ItemRequestDto itemRequestDto, @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Получен запрос на создание запроса с описанием: {}", itemRequestDto.getDescription());
        return itemRequestService.createRequest(userId,itemRequestDto);
    }

    @GetMapping
    List<ItemRequestResponseDto> getAllByUser(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemRequestService.getUserRequests(userId);
    }

    @GetMapping("all")
    List<ItemRequestResponseDto> getAll(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemRequestService.getAllRequests(userId);
    }

    @GetMapping("/{requestId}")
    ItemRequestResponseDto getById(@PathVariable Long requestId) {
       return itemRequestService.getRequestById(requestId);
    }


}
