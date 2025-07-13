package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.dto.ItemDto;

import java.time.LocalDateTime;
import java.util.Collection;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemRequestResponseDto {
    private Long id;
    private String description;
    private Long requestorId;
    private LocalDateTime created;
    private Collection<ItemDto> items;
}
