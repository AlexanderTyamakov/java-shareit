package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import org.springframework.transaction.annotation.Transactional;
import java.util.List;

public interface UserService {

    List<UserDto> getAllUsers();

    UserDto getUserById(long id);

    UserDto saveUser(UserDto userDto);

    @Transactional
    UserDto updateUser(long id, UserDto userDto);

    @Transactional
    UserDto deleteUser(long id);

}
