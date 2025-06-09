package ru.practicum.shareit.user.dao;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.User;

import java.util.Collection;
import java.util.HashMap;

@Repository
public class UserDaoInMemoryImpl implements UserDao {

    HashMap<Long, User> users = new HashMap<>();

    Long count = 0L;

    @Override
    public Collection<User> getAll() {
        return users.values();
    }

    @Override
    public User getOne(Long id) {
        if (users.containsKey(id)) {
            return users.get(id);
        }
        throw new NotFoundException(String.format("Пользователь с id %d не найден", id));
    }

    @Override
    public User create(User user) {
        user.setId(getNextId());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User update(User user) {
        users.replace(user.getId(), user);
        return getOne(user.getId());
    }

    @Override
    public void delete(Long id) {
        users.remove(id);
    }

    private Long getNextId() {
        return ++count;
    }

    public boolean isEmailExists(String email) {
        return users.values().stream()
                .anyMatch(user -> user.getEmail().equals(email));
    }
}
