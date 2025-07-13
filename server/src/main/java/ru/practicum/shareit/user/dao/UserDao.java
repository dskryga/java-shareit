package ru.practicum.shareit.user.dao;

import ru.practicum.shareit.user.User;

import java.util.Collection;

public interface UserDao {
    Collection<User> getAll();

    User getOne(Long id);

    User create(User user);

    User update(User user);

    void delete(Long id);

    boolean isEmailExists(String email);
}
