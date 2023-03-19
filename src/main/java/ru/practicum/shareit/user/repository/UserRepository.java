package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoPatch;

import java.util.List;

public interface UserRepository {

    List<User> findAll();

    User getById(long id);

    User save(UserDto userDto);

    User update(long id, UserDtoPatch userDto);

    User delete(long id);

}
