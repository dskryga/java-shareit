package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dao.UserDao;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserDao userDao;
    private final UserRepository userRepository;


    public Collection<UserDto> getAll() {
        return userRepository.findAll().stream()
                .map(UserMapper::mapToUserDto)
                .collect(Collectors.toList());
    }

    public UserDto getOne(Long id) {
        User user = getUserOrThrow(id);
        return UserMapper.mapToUserDto(user);
    }

    public UserDto create(UserDto userDto) {
        User userToCreate = UserMapper.mapToUser(userDto);
        return UserMapper.mapToUserDto(userRepository.save(userToCreate));
    }

    public UserDto update(UserDto userDto, Long id) {
        User origin = getUserOrThrow(id);
        if (userRepository.existsByEmail(userDto.getEmail()) &&
                !userDto.getEmail().equals(origin.getEmail()))
            throw new ValidationException(
                    String.format("Email %s уже используется другим пользователем", userDto.getEmail()));
        if (userDto.getName() != null) origin.setName(userDto.getName());
        if (userDto.getEmail() != null) origin.setEmail(userDto.getEmail());
        User updatedUser = userRepository.save(origin);
        return UserMapper.mapToUserDto(updatedUser);
    }

    public void delete(Long id) {
        userRepository.deleteById(id);
    }

    private User getUserOrThrow(Long userId) {
        return userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException(String.format("Пользователь с id %d не найден", userId)));
    }

}
