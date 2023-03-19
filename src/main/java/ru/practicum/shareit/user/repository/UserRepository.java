package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;
import java.util.Optional;

public interface UserRepository {

    List<User> findAll();

    Optional<User> getById(long id);

    Optional<User> save(UserDto userDto);

    Optional<User> update(long id, UserDto userDto);

    Optional<User> delete(long id);

}
