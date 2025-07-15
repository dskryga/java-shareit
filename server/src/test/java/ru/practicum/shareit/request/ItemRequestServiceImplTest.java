package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRequestRepository itemRequestRepository;

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;

    private final User user = new User(1L, "User", "user@email.com");
    private final ItemRequestDto requestDto = new ItemRequestDto("Need a drill");
    private final ItemRequest request = new ItemRequest(1L, "Need a drill", user, LocalDateTime.now());
    private final ItemRequestResponseDto responseDto = new ItemRequestResponseDto(
            1L, "Need a drill",1L, LocalDateTime.now(), Collections.emptyList());
    private final Item item = new Item(1L, "Drill", "Powerful drill", true, user.getId(), request.getId());
    private final ItemDto itemDto = new ItemDto(1L, "Drill", "Powerful drill", true, request.getId());

    @Test
    void createRequest_ShouldReturnCreatedRequest() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRequestRepository.save(any(ItemRequest.class))).thenReturn(request);

        ItemRequestResponseDto result = itemRequestService.createRequest(1L, requestDto);

        assertNotNull(result);
        assertEquals(request.getId(), result.getId());
        assertEquals(request.getDescription(), result.getDescription());
        verify(itemRequestRepository, times(1)).save(any(ItemRequest.class));
    }

    @Test
    void createRequest_WhenUserNotFound_ShouldThrowNotFoundException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemRequestService.createRequest(1L, requestDto));
    }

    @Test
    void getUserRequests_ShouldReturnRequestsWithItems() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRequestRepository.findByRequestorIdOrderByCreatedDesc(anyLong()))
                .thenReturn(List.of(request));
        when(itemRepository.findByRequestIdIn(anyList()))
                .thenReturn(List.of(item));

        List<ItemRequestResponseDto> result = itemRequestService.getUserRequests(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(request.getId(), result.get(0).getId());
        assertEquals(1, result.get(0).getItems().size());
    }

    @Test
    void getUserRequests_WhenNoRequests_ShouldReturnEmptyList() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRequestRepository.findByRequestorIdOrderByCreatedDesc(anyLong()))
                .thenReturn(Collections.emptyList());

        List<ItemRequestResponseDto> result = itemRequestService.getUserRequests(1L);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getAllRequests_ShouldReturnOtherUsersRequests() {
        when(itemRequestRepository.findByRequestorIdNotOrderByCreatedDesc(anyLong()))
                .thenReturn(List.of(request));

        List<ItemRequestResponseDto> result = itemRequestService.getAllRequests(2L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(request.getId(), result.get(0).getId());
    }

    @Test
    void getRequestById_ShouldReturnRequestWithItems() {
        when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.of(request));
        when(itemRepository.findByRequestId(anyLong())).thenReturn(List.of(item));

        ItemRequestResponseDto result = itemRequestService.getRequestById(1L);

        assertNotNull(result);
        assertEquals(request.getId(), result.getId());
        assertEquals(1, result.getItems().size());
    }

    @Test
    void getRequestById_WhenNotFound_ShouldThrowNotFoundException() {
        when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemRequestService.getRequestById(1L));
    }

    @Test
    void getRequestById_WhenNoItems_ShouldReturnRequestWithEmptyItems() {
        when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.of(request));
        when(itemRepository.findByRequestId(anyLong())).thenReturn(Collections.emptyList());

        ItemRequestResponseDto result = itemRequestService.getRequestById(1L);

        assertNotNull(result);
        assertEquals(request.getId(), result.getId());
        assertTrue(result.getItems().isEmpty());
    }
}