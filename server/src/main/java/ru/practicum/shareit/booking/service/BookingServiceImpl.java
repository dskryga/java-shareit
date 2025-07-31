package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.AccessDeniedException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public BookingResponseDto create(BookingDto bookingDto, Long userId) {
        User booker = getUserOrThrow(userId);

        Item item = getItemOrThrow(bookingDto.getItemId());

        if (!item.getAvailable())
            throw new ValidationException(String.format("Вещь с id %d недоступна для бронирования",
                    item.getId()));

        Booking booking = BookingMapper.mapToBooking(bookingDto);
        booking.setBooker(booker);
        booking.setItem(item);
        return BookingMapper.mapToBookingResponseDto(bookingRepository.save(booking));
    }

    @Override
    public BookingResponseDto getBookingById(Long bookingId, Long userId) {

        getUserOrThrow(userId);

        Booking booking = getBookingOrThrow(bookingId);

        Long bookerId = booking.getBooker().getId();
        Long itemOwnerId = booking.getItem().getOwnerId();

        if (userId.equals(bookerId) || userId.equals(itemOwnerId)) {
            return BookingMapper.mapToBookingResponseDto(booking);
        }
        throw new AccessDeniedException(String.format("Информация о бронировании с id %d недоступна" +
                " для пользователя с id %d", bookingId, userId));
    }

    @Override
    public BookingResponseDto aprroveBooking(Long bookingId, Long userId, boolean approved) {
        // Здесь пришлось оставить без внутреннего метода т.к. постман тест требует код 403,
        // приходится выбрасывать другое исключение
        userRepository.findById(userId).orElseThrow(() ->
                new AccessDeniedException(String.format("Пользователь с id %d не найден", userId)));

        Booking booking = getBookingOrThrow(bookingId);

        if (userId.equals(booking.getItem().getOwnerId())) {
            if (booking.getStatus() != BookingStatus.WAITING)
                throw new ValidationException("Статус бронирования не WAITING");
            booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
            return BookingMapper.mapToBookingResponseDto(bookingRepository.save(booking));
        }
        throw new AccessDeniedException(String.format("Информация о бронировании с id %d недоступна" +
                " для пользователя с id %d", bookingId, userId));
    }

    @Override
    public Collection<BookingResponseDto> findByBookerAndState(Long userId, BookingState bookingState) {
        getUserOrThrow(userId);
        return bookingRepository.findByBookerAndState(userId, bookingState.name()).stream()
                .map(BookingMapper::mapToBookingResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<BookingResponseDto> findByOwnerAndState(Long userId, BookingState bookingState) {
        getUserOrThrow(userId);
        return bookingRepository.findByOwnerAndState(userId, bookingState.name()).stream()
                .map(BookingMapper::mapToBookingResponseDto)
                .collect(Collectors.toList());
    }

    private User getUserOrThrow(Long userId) {
        return userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException(String.format("Пользователь с id %d не найден", userId)));
    }

    private Item getItemOrThrow(Long itemId) {
        return itemRepository.findById(itemId).orElseThrow(() ->
                new NotFoundException(String.format("Вещь с id %d не найдена", itemId)));
    }

    private Booking getBookingOrThrow(Long bookingId) {
        return bookingRepository.findById(bookingId).orElseThrow(() ->
                new NotFoundException(String.format("Бронирование с id %d не найдено", bookingId)));
    }
}
