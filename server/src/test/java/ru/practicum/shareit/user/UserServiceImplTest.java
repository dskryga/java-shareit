package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private final UserDto userDto = new UserDto(1L, "John Doe", "john@example.com");
    private final User user = User.builder()
            .id(1L)
            .name("John Doe")
            .email("john@example.com")
            .build();

    @Test
    void getAll_ShouldReturnEmptyList() {
        when(userRepository.findAll()).thenReturn(Collections.emptyList());

        Collection<UserDto> result = userService.getAll();

        assertTrue(result.isEmpty());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void getAll_ShouldReturnUserList() {
        when(userRepository.findAll()).thenReturn(List.of(user));

        Collection<UserDto> result = userService.getAll();

        assertEquals(1, result.size());
        assertEquals(userDto, result.iterator().next());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void getOne_ShouldReturnUser() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        UserDto result = userService.getOne(1L);

        assertEquals(userDto, result);
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void getOne_ShouldThrowNotFoundException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.getOne(1L));
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void create_ShouldReturnCreatedUser() {
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserDto result = userService.create(userDto);

        assertEquals(userDto, result);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void update_ShouldUpdateName() {
        UserDto updateDto = new UserDto(1L, "New Name", null);
        User updatedUser = User.builder()
                .id(1L)
                .name("New Name")
                .email("john@example.com")
                .build();

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);

        UserDto result = userService.update(updateDto, 1L);

        assertEquals("New Name", result.getName());
        assertEquals("john@example.com", result.getEmail());
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void update_ShouldUpdateEmail() {
        UserDto updateDto = new UserDto(1L, null, "new@example.com");
        User updatedUser = User.builder()
                .id(1L)
                .name("John Doe")
                .email("new@example.com")
                .build();

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);

        UserDto result = userService.update(updateDto, 1L);

        assertEquals("John Doe", result.getName());
        assertEquals("new@example.com", result.getEmail());
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).existsByEmail("new@example.com");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void update_ShouldThrowValidationExceptionWhenEmailExists() {
        UserDto updateDto = new UserDto(1L, null, "existing@example.com");
        User existingUser = User.builder().id(2L).email("existing@example.com").build();

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        assertThrows(ValidationException.class, () -> userService.update(updateDto, 1L));
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).existsByEmail("existing@example.com");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void delete_ShouldCallRepositoryDelete() {
        doNothing().when(userRepository).deleteById(anyLong());

        userService.delete(1L);

        verify(userRepository, times(1)).deleteById(1L);
    }
}