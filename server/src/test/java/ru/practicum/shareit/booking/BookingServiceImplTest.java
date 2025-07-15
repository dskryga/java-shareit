package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.AccessDeniedException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookingServiceImplTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private User user;
    private User owner;
    private Item item;
    private Booking booking;
    private BookingDto bookingDto;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setName("User");
        user.setEmail("user@example.com");

        owner = new User();
        owner.setId(2L);
        owner.setName("Owner");
        owner.setEmail("owner@example.com");

        item = new Item();
        item.setId(1L);
        item.setName("Item");
        item.setDescription("Description");
        item.setAvailable(true);
        item.setOwnerId(owner.getId());

        bookingDto = new BookingDto();
        bookingDto.setItemId(item.getId());
        bookingDto.setStart(LocalDateTime.now().plusHours(1));
        bookingDto.setEnd(LocalDateTime.now().plusHours(2));

        booking = new Booking();
        booking.setId(1L);
        booking.setBooker(user);
        booking.setItem(item);
        booking.setStatus(BookingStatus.WAITING);
        booking.setStartTime(bookingDto.getStart());
        booking.setEndTime(bookingDto.getEnd());
    }

    @Test
    void create_shouldCreateBooking() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        BookingResponseDto result = bookingService.create(bookingDto, user.getId());

        assertNotNull(result);
        assertEquals(booking.getId(), result.getId());
        verify(bookingRepository, times(1)).save(any(Booking.class));
    }

    @Test
    void create_shouldThrowNotFoundExceptionWhenUserNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingService.create(bookingDto, user.getId()));
    }

    @Test
    void create_shouldThrowNotFoundExceptionWhenItemNotFound() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingService.create(bookingDto, user.getId()));
    }

    @Test
    void create_shouldThrowValidationExceptionWhenItemNotAvailable() {
        item.setAvailable(false);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));

        assertThrows(ValidationException.class, () -> bookingService.create(bookingDto, user.getId()));
    }

    @Test
    void create_shouldThrowValidationExceptionWhenStartAfterEnd() {
        bookingDto.setStart(LocalDateTime.now().plusHours(2));
        bookingDto.setEnd(LocalDateTime.now().plusHours(1));
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));

        assertThrows(ValidationException.class, () -> bookingService.create(bookingDto, user.getId()));
    }

    @Test
    void getBookingById_shouldReturnBooking() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));

        BookingResponseDto result = bookingService.getBookingById(booking.getId(), user.getId());

        assertNotNull(result);
        assertEquals(booking.getId(), result.getId());
    }

    @Test
    void getBookingById_shouldThrowNotFoundExceptionWhenBookingNotFound() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingService.getBookingById(999L, user.getId()));
    }

    @Test
    void getBookingById_shouldThrowAccessDeniedExceptionWhenUserNotOwnerOrBooker() {
        when(userRepository.findById(3L)).thenReturn(Optional.of(new User()));
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));

        assertThrows(AccessDeniedException.class, () -> bookingService.getBookingById(booking.getId(), 3L));
    }

    @Test
    void approveBooking_shouldApproveBooking() {
        when(userRepository.findById(owner.getId())).thenReturn(Optional.of(owner));
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        BookingResponseDto result = bookingService.aprroveBooking(booking.getId(), owner.getId(), true);

        assertNotNull(result);
        assertEquals(BookingStatus.APPROVED, result.getStatus());
        verify(bookingRepository, times(1)).save(any(Booking.class));
    }

    @Test
    void approveBooking_shouldRejectBooking() {
        when(userRepository.findById(owner.getId())).thenReturn(Optional.of(owner));
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        BookingResponseDto result = bookingService.aprroveBooking(booking.getId(), owner.getId(), false);

        assertNotNull(result);
        assertEquals(BookingStatus.REJECTED, result.getStatus());
        verify(bookingRepository, times(1)).save(any(Booking.class));
    }

    @Test
    void approveBooking_shouldThrowValidationExceptionWhenStatusNotWaiting() {
        booking.setStatus(BookingStatus.APPROVED);
        when(userRepository.findById(owner.getId())).thenReturn(Optional.of(owner));
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));

        assertThrows(ValidationException.class, () ->
                bookingService.aprroveBooking(booking.getId(), owner.getId(), true));
    }

    @Test
    void approveBooking_shouldThrowAccessDeniedExceptionWhenUserNotOwner() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));

        assertThrows(AccessDeniedException.class, () ->
                bookingService.aprroveBooking(booking.getId(), user.getId(), true));
    }

    @Test
    void findByBookerAndState_shouldReturnBookings() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(bookingRepository.findByBookerAndState(user.getId(), BookingState.ALL.name()))
                .thenReturn(List.of(booking));

        Collection<BookingResponseDto> result = bookingService.findByBookerAndState(user.getId(), BookingState.ALL);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void findByOwnerAndState_shouldReturnBookings() {
        when(userRepository.findById(owner.getId())).thenReturn(Optional.of(owner));
        when(bookingRepository.findByOwnerAndState(owner.getId(), BookingState.ALL.name()))
                .thenReturn(List.of(booking));

        Collection<BookingResponseDto> result = bookingService.findByOwnerAndState(owner.getId(), BookingState.ALL);

        assertNotNull(result);
        assertEquals(1, result.size());
    }
}