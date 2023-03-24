package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoMapper;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public List<UserDto> getAllUsers() {
        log.info("Возвращен список всех пользователей");
        return userRepository.findAll().stream().map(UserDtoMapper::toUserDto).collect(Collectors.toList());
    }

    @Override
    public UserDto getUserById(long id) {
        log.info("Поиск пользователя с id={}", id);
        Optional<User> found = userRepository.findById(id);
        if (found.isEmpty()) {
            throw new UserNotFoundException("Пользователь с id=" + id + " не найден");
        } else {
            log.info("Пользователь с id=" + id + " получен");
            return UserDtoMapper.toUserDto(found.get());
        }
    }

    @Override
    public UserDto saveUser(UserDto userDto) {
        log.info("Сохранение пользователя");
        User saved;
        try {
            saved = userRepository.save(UserDtoMapper.toNewUser(userDto));
        } catch (DataIntegrityViolationException e) {
            throw new ValidationException("Пользователь с email=" + userDto.getEmail() + " уже есть в коллекции");
        }
        return UserDtoMapper.toUserDto(saved);
    }

    @Override
    public UserDto updateUser(long id, UserDto userDto) {
        log.info("Изменение пользователя с id={}", id);
        Optional<User> found = userRepository.findById(id);
        if (found.isEmpty()) {
            throw new UserNotFoundException("Пользователь с id=" + id + " не найден");
        }
        if (userRepository.getEmailsExceptUserById(id).contains(userDto.getEmail())) {
            throw new ValidationException("Пользователь с email=" + userDto.getEmail() + " уже есть в коллекции");
        }
        User toUpdate = UserDtoMapper.patchToUser(userDto, found.get(), id);
        userRepository.updateUserById(toUpdate.getName(), toUpdate.getEmail(), id);
        log.info("Пользователь с id=" + id + " обновлен");
        return UserDtoMapper.toUserDto(toUpdate);
    }

    @Override
    public UserDto deleteUser(long id) {
        log.info("Удаление пользователя с id={}", id);
        Optional<User> found = userRepository.findById(id);
        if (found.isEmpty()) {
            throw new UserNotFoundException("Пользователь с id=" + id + " не найден");
        }
        userRepository.deleteById(id);
        log.info("Пользователь с id=" + id + " удален");
        return UserDtoMapper.toUserDto(found.get());
    }
}
