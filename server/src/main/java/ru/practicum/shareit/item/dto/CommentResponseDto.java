package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommentResponseDto {
    private Long id;
    private String text;
    private String authorName;
    private Long itemId;
    private LocalDateTime created;

    public CommentResponseDto(Long id, String text, String authorName, Long itemId) {
        this.id = id;
        this.text = text;
        this.authorName = authorName;
        this.itemId = itemId;
        created = LocalDateTime.now();
    }
}
