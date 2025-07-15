package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final UserRepository userRepository;
    private final ItemRequestRepository itemRequestRepository;
    private final ItemRepository itemRepository;

    @Override
    public ItemRequestResponseDto createRequest(Long userId, ItemRequestDto itemRequestDto) {
        ItemRequest itemRequest = ItemRequestMapper.mapToItemRequest(itemRequestDto);
        User requestor = getUserOrThrow(userId);
        itemRequest.setRequestor(requestor);
        itemRequest.setCreated(LocalDateTime.now());
        return ItemRequestMapper.mapToItemRequestResponseDto(itemRequestRepository.save(itemRequest));
    }

    @Override
    public List<ItemRequestResponseDto> getUserRequests(Long userId) {
        getUserOrThrow(userId);
        List<ItemRequest> requests = itemRequestRepository.findByRequestorIdOrderByCreatedDesc(userId);

        List<Long> requestIds = requests.stream()
                .map(ItemRequest::getId)
                .collect(Collectors.toList());

        Map<Long, List<ItemDto>> itemsByRequestId = itemRepository.findByRequestIdIn(requestIds)
                .stream()
                .map(item -> ItemMapper.mapToDto(item))
                .collect(Collectors.groupingBy(ItemDto::getRequestId));

        return requests.stream()
                .map(request -> {
                    ItemRequestResponseDto dto = ItemRequestMapper.mapToItemRequestResponseDto(request);
                    dto.setItems(itemsByRequestId.getOrDefault(request.getId(), Collections.emptyList()));
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemRequestResponseDto> getAllRequests(Long userId) {
        return itemRequestRepository.findByRequestorIdNotOrderByCreatedDesc(userId).stream()
                .map(ItemRequestMapper::mapToItemRequestResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemRequestResponseDto getRequestById(Long requestId) {
        ItemRequest itemRequest = itemRequestRepository.findById(requestId).orElseThrow(() ->
                new NotFoundException(String.format("Запрос с id %d не найден", requestId)));
        List<ItemDto> items = itemRepository.findByRequestId(requestId).stream()
                .map(ItemMapper::mapToDto)
                .collect(Collectors.toList());
        ItemRequestResponseDto dto = ItemRequestMapper.mapToItemRequestResponseDto(itemRequest);
        dto.setItems(items);
        return dto;
    }

    private User getUserOrThrow(Long userId) {
        return userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException(String.format("Пользователь с id %d не найден", userId)));
    }
}
