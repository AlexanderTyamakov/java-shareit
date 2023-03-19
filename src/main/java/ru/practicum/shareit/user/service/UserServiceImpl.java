package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoPatch;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public List<User> getAllUsers() {
        log.info("Возвращен список пользователей");
        return userRepository.findAll();
    }

    @Override
    public User getUserById(long id) {
        log.info("Поиск пользователя с id={}", id);
        return userRepository.getById(id);
    }

    @Override
    public User saveUser(UserDto userDto) {
        log.info("Сохранение пользователя");
        return userRepository.save(userDto);
    }

    @Override
    public User updateUser(long id, UserDtoPatch userDto) {
        log.info("Изменения пользователя с id={}", id);
        return userRepository.update(id, userDto);
    }

    @Override
    public User deleteUser(long id) {
        log.info("Удаление пользователя с id={}", id);
        return userRepository.delete(id);
    }
}
