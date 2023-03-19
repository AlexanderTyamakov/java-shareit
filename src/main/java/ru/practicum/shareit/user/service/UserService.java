package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoPatch;

import java.util.List;

public interface UserService {

    List<User> getAllUsers();

    User getUserById(long id);

    User saveUser(UserDto user);

    User updateUser(long id, UserDtoPatch user);

    User deleteUser(long id);

}
