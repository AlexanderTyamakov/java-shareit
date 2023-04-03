package ru.practicum.shareit.user;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoMapper;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    private UserRepository mockUserRepository;
    private UserService userService;
    private UserDto userDto = new UserDto(1L, "Ivan", "ivan@ivan.ru");

    @BeforeEach
    void beforeEach() {
        userService = new UserServiceImpl(mockUserRepository);
    }

    @Test
    void shouldGetExceptionWhenGetUserWithWrongId() {
        when(mockUserRepository.findById(any(Long.class)))
                .thenReturn(Optional.empty());
        final UserNotFoundException exception = Assertions.assertThrows(
                UserNotFoundException.class,
                () -> userService.getUserById(-1L));
        Assertions.assertEquals("Пользователь с id = -1 не найден", exception.getMessage());
    }

    @Test
    void shouldGetExceptionWhenCreateUserWithExistEmail() {
        when(mockUserRepository.save(any()))
                .thenThrow(new DataIntegrityViolationException(""));
        final ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                () -> userService.saveUser(userDto));
        Assertions.assertEquals("Пользователь с email = " + userDto.getEmail() + " уже есть в коллекции",
                exception.getMessage());
    }

    @Test
    void shouldReturnUserWhenFindUserById() {
        when(mockUserRepository.findById(any(Long.class)))
                .thenReturn(Optional.of(UserDtoMapper.toNewUser(userDto)));
        UserDto userDtoReturned = userService.getUserById(1L);
        verify(mockUserRepository, Mockito.times(1))
                .findById(1L);
        assertThat(userDtoReturned.getName(), equalTo(userDto.getName()));
        assertThat(userDtoReturned.getEmail(), equalTo(userDto.getEmail()));
    }
}
