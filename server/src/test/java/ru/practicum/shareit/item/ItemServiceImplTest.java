package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.AccessDeniedException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private ItemRequestRepository itemRequestRepository;

    @InjectMocks
    private ItemServiceImpl itemService;

    private final User user = new User(1L, "User", "user@email.com");
    private final Item item = new Item(1L, "Item", "Description", true, 1L, null);
    private final ItemDto itemDto = new ItemDto(1L, "Item", "Description", true, 1L);
    private final Booking booking = new Booking(1L, LocalDateTime.now().minusDays(2),
            LocalDateTime.now().minusDays(1), item, user, BookingStatus.APPROVED);
    private final Comment comment = new Comment(1L, "Comment text", item, user, LocalDateTime.now());
    private final CommentRequestDto commentRequestDto = new CommentRequestDto("Comment text");
    private final CommentResponseDto commentResponseDto = new CommentResponseDto(1L, "Comment text", "User", null);
    private final ItemRequest itemRequest = new ItemRequest(1L, "need a drill", user, LocalDateTime.now());

    @Test
    void createItem_ShouldReturnItemDto() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.save(any(Item.class))).thenReturn(item);
        when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.of(itemRequest));

        ItemDto result = itemService.create(itemDto, 1L);

        assertNotNull(result);
        assertEquals(itemDto.getId(), result.getId());
        assertEquals(itemDto.getName(), result.getName());
        verify(itemRepository, times(1)).save(any(Item.class));
    }

    @Test
    void createItem_WhenUserNotFound_ShouldThrowNotFoundException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.create(itemDto, 1L));
    }

    @Test
    void getOne_ShouldReturnItemWithBookings() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(bookingRepository.findTopByItemIdAndEndTimeBeforeAndStatusOrderByEndTimeDesc(anyLong(),
                any(LocalDateTime.class), any(BookingStatus.class))).thenReturn(Optional.empty());
        when(bookingRepository.findTopByItemIdAndEndTimeAfterAndStatusOrderByEndTimeAsc(anyLong(),
                any(LocalDateTime.class), any(BookingStatus.class))).thenReturn(Optional.empty());
        when(commentRepository.findAllByItemIdOrderByCreatedTimeAsc(anyLong())).thenReturn(Collections.emptyList());

        ItemDtoWithBookings result = itemService.getOne(1L);

        assertNotNull(result);
        assertEquals(item.getId(), result.getId());
        assertEquals(item.getName(), result.getName());
        assertNull(result.getLastBooking());
        assertNull(result.getNextBooking());
        assertTrue(result.getComments().isEmpty());
    }

    @Test
    void getOne_WhenItemNotFound_ShouldThrowNotFoundException() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.getOne(1L));
    }

    @Test
    void updateItem_ShouldReturnUpdatedItem() {
        ItemDto updatedDto = new ItemDto(1L, "Updated", "Updated description", false, 1L);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(itemRepository.save(any(Item.class))).thenReturn(item);

        ItemDto result = itemService.update(updatedDto, 1L, 1L);

        assertNotNull(result);
        assertEquals(updatedDto.getName(), result.getName());
        assertEquals(updatedDto.getDescription(), result.getDescription());
        assertEquals(updatedDto.getAvailable(), result.getAvailable());
    }

    @Test
    void updateItem_WhenUserNotOwner_ShouldThrowAccessDeniedException() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        assertThrows(AccessDeniedException.class, () -> itemService.update(itemDto, 2L, 1L));
    }

    @Test
    void getAllByOwner_ShouldReturnListOfItemsWithBookings() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findAllByOwnerId(anyLong())).thenReturn(List.of(item));
        when(bookingRepository.findApprovedBookingsForItems(anyList())).thenReturn(Collections.emptyList());
        when(commentRepository.findAllByItemIdIn(anyList())).thenReturn(Collections.emptyList());

        Collection<ItemDtoWithBookings> result = itemService.getAllByOwner(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        ItemDtoWithBookings firstItem = result.iterator().next();
        assertEquals(item.getId(), firstItem.getId());
        assertNull(firstItem.getLastBooking());
        assertNull(firstItem.getNextBooking());
        assertTrue(firstItem.getComments().isEmpty());
    }

    @Test
    void getAllSearchedItems_ShouldReturnFilteredItems() {
        when(itemRepository.findByNameContainingIgnoreCaseAndAvailableTrue(anyString()))
                .thenReturn(List.of(item));

        Collection<ItemDto> result = itemService.getAllSearchedItems("test");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(item.getId(), result.iterator().next().getId());
    }

    @Test
    void getAllSearchedItems_WhenTextIsBlank_ShouldReturnEmptyList() {
        Collection<ItemDto> result = itemService.getAllSearchedItems(" ");

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void createComment_ShouldReturnCommentResponseDto() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(bookingRepository.findCompletedApprovedBookingsForUserAndItem(anyLong(), anyLong()))
                .thenReturn(List.of(booking));
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        CommentResponseDto result = itemService.createComment(1L, 1L, commentRequestDto);

        assertNotNull(result);
        assertEquals(comment.getId(), result.getId());
        assertEquals(comment.getText(), result.getText());
        assertEquals(user.getName(), result.getAuthorName());
    }

    @Test
    void createComment_WhenUserHasNoBookings_ShouldThrowValidationException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(bookingRepository.findCompletedApprovedBookingsForUserAndItem(anyLong(), anyLong()))
                .thenReturn(Collections.emptyList());

        assertThrows(ValidationException.class,
                () -> itemService.createComment(1L, 1L, commentRequestDto));
    }
}