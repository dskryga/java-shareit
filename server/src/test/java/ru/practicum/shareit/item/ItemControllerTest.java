package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBookings;
import ru.practicum.shareit.item.service.ItemService;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private ItemService itemService;

    private final ItemDto itemDto = new ItemDto(1L, "Item", "Description", true, 1L);
    private final ItemDtoWithBookings itemDtoWithBookings = new ItemDtoWithBookings(
            1L, "Item", "Description", true, null, null, Collections.emptyList());
    private final CommentRequestDto commentRequestDto = new CommentRequestDto("Comment text");
    private final CommentResponseDto commentResponseDto = new CommentResponseDto(1L, "Comment text", "Author", null);

    @Test
    void createItemTest() throws Exception {
        when(itemService.create(any(ItemDto.class), anyLong()))
                .thenReturn(itemDto);

        mockMvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())));
    }

    @Test
    void getItemByIdTest() throws Exception {
        when(itemService.getOne(anyLong()))
                .thenReturn(itemDtoWithBookings);

        mockMvc.perform(get("/items/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDtoWithBookings.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDtoWithBookings.getName())))
                .andExpect(jsonPath("$.description", is(itemDtoWithBookings.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDtoWithBookings.getAvailable())));
    }

    @Test
    void updateItemTest() throws Exception {
        when(itemService.update(any(ItemDto.class), anyLong(), anyLong()))
                .thenReturn(itemDto);

        mockMvc.perform(patch("/items/1")
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())));
    }

    @Test
    void getAllItemsByOwnerTest() throws Exception {
        when(itemService.getAllByOwner(anyLong()))
                .thenReturn(List.of(itemDtoWithBookings));

        mockMvc.perform(get("/items")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(itemDtoWithBookings.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(itemDtoWithBookings.getName())))
                .andExpect(jsonPath("$[0].description", is(itemDtoWithBookings.getDescription())))
                .andExpect(jsonPath("$[0].available", is(itemDtoWithBookings.getAvailable())));
    }

    @Test
    void getAllSearchedItemsTest() throws Exception {
        when(itemService.getAllSearchedItems(anyString()))
                .thenReturn(List.of(itemDto));

        mockMvc.perform(get("/items/search?text=test")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(itemDto.getName())))
                .andExpect(jsonPath("$[0].description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$[0].available", is(itemDto.getAvailable())));
    }

    @Test
    void addCommentTest() throws Exception {
        when(itemService.createComment(anyLong(), anyLong(), any(CommentRequestDto.class)))
                .thenReturn(commentResponseDto);

        mockMvc.perform(post("/items/1/comment")
                        .content(mapper.writeValueAsString(commentRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(commentResponseDto.getId()), Long.class))
                .andExpect(jsonPath("$.text", is(commentResponseDto.getText())))
                .andExpect(jsonPath("$.authorName", is(commentResponseDto.getAuthorName())));
    }
}