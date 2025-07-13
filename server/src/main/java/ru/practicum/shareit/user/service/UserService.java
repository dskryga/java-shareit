package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;

public interface UserService {
    Collection<UserDto> getAll();

    UserDto getOne(Long id);

    UserDto create(UserDto userDto);

    UserDto update(UserDto userDto, Long id);

    void delete(Long id);
}
