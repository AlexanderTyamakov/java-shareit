package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoMapper;

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
        User found = handleOptionalUser(userRepository.findById(id), id);
        log.info("Пользователь с id=" + id + " получен");
        return UserDtoMapper.toUserDto(found);
    }

    @Override
    public UserDto saveUser(UserDto userDto) {
        log.info("Сохранение пользователя");
        User saved;
        try {
            saved = userRepository.save(UserDtoMapper.toNewUser(userDto));
        } catch (DataIntegrityViolationException e) {
            throw new ValidationException("Пользователь с email = " + userDto.getEmail() + " уже есть в коллекции");
        }
        log.info("Сохранен пользователь с id = " + saved.getId());
        return UserDtoMapper.toUserDto(saved);
    }

    @Override
    public UserDto updateUser(long id, UserDto userDto) {
        log.info("Изменение пользователя с id={}", id);
        User found = handleOptionalUser(userRepository.findById(id), id);
        if (userRepository.getAllEmailsExceptUserById(id).contains(userDto.getEmail())) {
            throw new ValidationException("Пользователь с email = " + userDto.getEmail() + " уже есть в коллекции");
        }
        User toUpdate = UserDtoMapper.patchToUser(userDto, found, id);
        userRepository.updateUserById(toUpdate.getName(), toUpdate.getEmail(), id);
        log.info("Обновлен пользователь с id = " + id);
        return UserDtoMapper.toUserDto(toUpdate);
    }

    @Override
    public UserDto deleteUser(long id) {
        log.info("Удаление пользователя с id={}", id);
        User found = handleOptionalUser(userRepository.findById(id), id);
        userRepository.deleteById(id);
        log.info("Пользователь с id = " + id + " удален");
        return UserDtoMapper.toUserDto(found);
    }

    private User handleOptionalUser(Optional<User> user, long id) {
        if (user.isEmpty()) {
            throw new UserNotFoundException("Пользователь с id = " + id + " не найден");
        }
        return user.orElseThrow();
    }
}
