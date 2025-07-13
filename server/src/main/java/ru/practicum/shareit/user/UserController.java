package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collection;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    @PostMapping
    public UserDto create(@RequestBody @Valid UserDto userDto) {
        log.info("Получен запрос на создание пользователся с email {}", userDto.getEmail());
        return userService.create(userDto);
    }

    @GetMapping
    public Collection<UserDto> getAll() {
        log.info("Получен запрос на вывод всех пользователей");
        return userService.getAll();
    }

    @GetMapping("{id}")
    public UserDto getOne(@PathVariable Long id) {
        log.info("Получен запрос на получение информации о пользователе с id {}", id);
        return userService.getOne(id);
    }

    @DeleteMapping("{id}")
    public void delete(@PathVariable Long id) {
        log.info("Получен запрос на удаление информации о пользователе с id {}", id);
        userService.delete(id);
    }

    @PatchMapping("{id}")
    public UserDto update(@RequestBody UserDto userDto, @PathVariable Long id) {
        log.info("Получен запрос на изменение информации о пользователе с id {}", id);
        return userService.update(userDto, id);
    }
}
