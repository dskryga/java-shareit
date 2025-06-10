package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dao.UserDao;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserDao userDao;

    @Override
    public Collection<UserDto> getAll() {
        return userDao.getAll().stream()
                .map(UserMapper::mapToUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto getOne(Long id) {
        return UserMapper.mapToUserDto(userDao.getOne(id));
    }

    @Override
    public UserDto create(UserDto userDto) {
        if (userDao.isEmailExists(userDto.getEmail())) throw new ValidationException(
                String.format("Email %s уже используется", userDto.getEmail()));
        User userToCreate = UserMapper.mapToUser(userDto);
        return UserMapper.mapToUserDto(userDao.create(userToCreate));
    }

    @Override
    public UserDto update(UserDto userDto, Long id) {
        User origin = userDao.getOne(id);
        if (userDao.isEmailExists(userDto.getEmail()) &&
                !userDto.getEmail().equals(origin.getEmail()))
            throw new ValidationException(
                    String.format("Email %s уже используется другим пользователем", userDto.getEmail()));
        if (userDto.getName() != null) origin.setName(userDto.getName());
        if (userDto.getEmail() != null) origin.setEmail(userDto.getEmail());
        return UserMapper.mapToUserDto(userDao.update(origin));
    }

    @Override
    public void delete(Long id) {
        userDao.delete(id);
    }
}
