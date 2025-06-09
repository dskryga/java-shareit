package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;
import java.util.List;

public interface UserService {
    Collection<User> getAll();

    User getOne(Long id);

    User create(UserDto userDto);

    User update(UserDto userDto, Long id);

    void delete(Long id);
}
