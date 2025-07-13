package ru.practicum.shareit.handlers;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ErrorResponse {
    private String description;
    private Integer errorCode;
}
