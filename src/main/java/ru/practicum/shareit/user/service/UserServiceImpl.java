package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dao.UserDao;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;

import java.util.Collection;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService{

    private final UserDao userDao;

    @Override
    public Collection<User> getAll() {
        return userDao.getAll();
    }

    @Override
    public User getOne(Long id) {
        return userDao.getOne(id);
    }

    @Override
    public User create(UserDto userDto) {
        if(userDao.isEmailExists(userDto.getEmail())) throw new ValidationException(
                String.format("Email %s уже используется",userDto.getEmail()));
        User userToCreate = UserMapper.mapToUser(userDto);
        return userDao.create(userToCreate);
    }

    @Override
    public User update(UserDto userDto, Long id) {
        User origin = userDao.getOne(id);
        if(userDao.isEmailExists(userDto.getEmail())) throw new ValidationException(
                String.format("Email %s уже используется",userDto.getEmail()));
        if(userDto.getName()!= null) origin.setName(userDto.getName());
        if(userDto.getEmail()!=null) origin.setEmail(userDto.getEmail());
        return userDao.update(origin);
    }

    @Override
    public void delete(Long id) {
        userDao.delete(id);
    }
}
