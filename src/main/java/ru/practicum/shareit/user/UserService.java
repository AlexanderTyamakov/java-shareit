package ru.practicum.shareit.user;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.dto.UserDto;

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
