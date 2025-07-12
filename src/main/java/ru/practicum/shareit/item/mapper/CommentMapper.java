package ru.practicum.shareit.item.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.model.Comment;

@UtilityClass
public class CommentMapper {
    public Comment mapToComment(CommentRequestDto commentRequestDto) {
        Comment comment = new Comment();
        comment.setText(commentRequestDto.getText());
        return comment;
    }

    public CommentResponseDto mapToCommentResponseDto(Comment comment) {
        return CommentResponseDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .itemId(comment.getItem().getId())
                .created(comment.getCreatedTime())
                .authorName(comment.getAuthor().getName())
                .build();
    }
}
