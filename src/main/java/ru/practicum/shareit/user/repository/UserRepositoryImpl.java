package ru.practicum.shareit.user.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoMapper;
import ru.practicum.shareit.user.dto.UserDtoPatch;

import javax.validation.ValidationException;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
public class UserRepositoryImpl implements UserRepository {

    private final Map<Long, User> users = new HashMap<>();
    private long userRepositoryId;

    @Override
    public List<User> findAll() {
        log.info("Получены все пользователи");
        return users.values().stream()
                .sorted(Comparator.comparing(User::getId))
                .collect(Collectors.toList());
    }

    @Override
    public User getById(long id) {
        if (!users.containsKey(id)) {
            throw new UserNotFoundException("Пользователь с id=" + id + " не найден");
        }
        log.info("Пользователь с id=" + id + " получен");
        return users.get(id);
    }

    @Override
    public User save(UserDto userDto) {
        Set<String> emails = users.values().stream()
                .map(User::getEmail)
                .collect(Collectors.toSet());
        if (emails.contains(userDto.getEmail())) {
            throw new ValidationException("Пользователь с email=" + userDto.getEmail() + " уже есть в коллекции");
        }
        long id = getId();
        User user = UserDtoMapper.toUser(userDto, id);
        users.put(id, user);
        log.info("Пользователь с id=" + id + " сохранен");
        return user;
    }

    @Override
    public User update(long id, UserDtoPatch userDtoPatch) {
        if (!users.containsKey(id)) {
            throw new UserNotFoundException("Пользователь с id=" + id + " не найден");
        }
        Set<String> emails = users.values().stream()
                .filter(x -> x.getId() != id)
                .map(User::getEmail)
                .collect(Collectors.toSet());
        if (emails.contains(userDtoPatch.getEmail())) {
            throw new ValidationException("Пользователь с email=" + userDtoPatch.getEmail() + " уже есть в коллекции");
        }
        User user = UserDtoMapper.patchToUser(userDtoPatch, users.get(id), id);
        users.put(id, user);
        log.info("Пользователь с id=" + id + " обновлен");
        return user;
    }

    @Override
    public User delete(long id) {
        if (!users.containsKey(id)) {
            throw new UserNotFoundException("Пользователь с id=" + id + " не найден");
        }
        log.info("Пользователь с id=" + id + " удален");
        return users.remove(id);
    }

    private long getId() {
        return ++userRepositoryId;
    }
}
