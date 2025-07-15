package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.mapper.UserMapper;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private BookingService bookingService;

    private final User booker = new User(2L, "Booker", "booker@email.com");
    private final Item item = new Item(1L, "Item", "Description", true, 1L, null);
    private final BookingDto bookingDto = new BookingDto(
            1L,
            LocalDateTime.now().plusDays(1),
            LocalDateTime.now().plusDays(2),BookingStatus.WAITING);
    private final BookingResponseDto bookingResponseDto = new BookingResponseDto(
            1L,
            LocalDateTime.now().plusDays(1),
            LocalDateTime.now().plusDays(2),
            ItemMapper.mapToDto(item),
            UserMapper.mapToUserDto(booker),
            BookingStatus.WAITING);

    @Test
    void createBooking_ShouldReturnCreatedBooking() throws Exception {
        when(bookingService.create(any(BookingDto.class), anyLong()))
                .thenReturn(bookingResponseDto);

        mockMvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 2L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingResponseDto.getId()), Long.class))
                .andExpect(jsonPath("$.status", is(bookingResponseDto.getStatus().toString())));
    }

    @Test
    void getBookingById_ShouldReturnBooking() throws Exception {
        when(bookingService.getBookingById(anyLong(), anyLong()))
                .thenReturn(bookingResponseDto);

        mockMvc.perform(get("/bookings/1")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 2L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingResponseDto.getId()), Long.class))
                .andExpect(jsonPath("$.item.id", is(item.getId()), Long.class));
    }

    @Test
    void approveBooking_ShouldReturnApprovedBooking() throws Exception {
        BookingResponseDto approvedResponse = new BookingResponseDto(
                1L,
                bookingResponseDto.getStart(),
                bookingResponseDto.getEnd(),
                ItemMapper.mapToDto(item),
                UserMapper.mapToUserDto(booker),
                BookingStatus.APPROVED);

        when(bookingService.aprroveBooking(anyLong(), anyLong(), anyBoolean()))
                .thenReturn(approvedResponse);

        mockMvc.perform(patch("/bookings/1?approved=true")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(BookingStatus.APPROVED.toString())));
    }

    @Test
    void findByBookerAndState_ShouldReturnBookings() throws Exception {
        when(bookingService.findByBookerAndState(anyLong(), any(BookingState.class)))
                .thenReturn(List.of(bookingResponseDto));

        mockMvc.perform(get("/bookings?state=ALL")
                        .header("X-Sharer-User-Id", 2L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(bookingResponseDto.getId()), Long.class));
    }

    @Test
    void findByOwnerAndState_ShouldReturnBookings() throws Exception {
        when(bookingService.findByOwnerAndState(anyLong(), any(BookingState.class)))
                .thenReturn(List.of(bookingResponseDto));

        mockMvc.perform(get("/bookings/owner?state=ALL")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(bookingResponseDto.getId()), Long.class));
    }

    @Test
    void getBookingById_WithInvalidState_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/bookings?state=INVALID")
                        .header("X-Sharer-User-Id", 2L))
                .andExpect(status().isBadRequest());
    }
}