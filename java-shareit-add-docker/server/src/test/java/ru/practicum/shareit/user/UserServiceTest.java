package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoMapper;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserServiceTest {

    private final UserService userService;
    private User user1 = new User(1L, "Ivan", "ivan@ivan.ru");
    private User user2 = new User(2L, "Egor", "egor@egor.ru");
    private User user3 = new User(3L, "Alex", "alex@alex.ru");
    private User user4 = new User(4L, "Mike", "mike@mike.ru");
    private User user5 = new User(4L, "Jack", "alex@alex.ru");

    @Test
    void shouldReturnUserWhenGetUserById() {
        UserDto returnUserDto = userService.saveUser(UserDtoMapper.toUserDto(user1));
        assertThat(returnUserDto.getName(), equalTo(user1.getName()));
        assertThat(returnUserDto.getEmail(), equalTo(user1.getEmail()));
    }

    @Test
    void shouldDeleteUser() {
        User user = new User(10L, "Tom", "tom@tom.ru");
        UserDto returnUserDto = userService.saveUser(UserDtoMapper.toUserDto(user));
        List<UserDto> listUserBefore = userService.getAllUsers();
        userService.deleteUser(returnUserDto.getId());
        List<UserDto> listUserAfter = userService.getAllUsers();
        assertThat(listUserAfter.size(), equalTo(listUserBefore.size() - 1));
    }

    @Test
    void shouldGetExceptionWhenDeleteUserWithWrongId() {
        UserNotFoundException exp = assertThrows(UserNotFoundException.class, () -> userService.deleteUser(10L));
        assertEquals("Пользователь с id = 10 не найден", exp.getMessage());
    }

    @Test
    void shouldUpdateUser() {
        UserDto userBefore = userService.saveUser(UserDtoMapper.toUserDto(user1));
        userService.updateUser(userBefore.getId(), UserDtoMapper.toUserDto(user2));
        UserDto updatedUser = userService.getUserById(userBefore.getId());
        assertThat(updatedUser.getName(), equalTo("Egor"));
        assertThat(updatedUser.getEmail(), equalTo("egor@egor.ru"));
    }

    @Test
    void shouldGetExceptionWhenUpdateUserWithExistEmail() {
        UserDto returnUserSaved1 = userService.saveUser(UserDtoMapper.toUserDto(user3));
        UserDto returnUserSaved2 = userService.saveUser(UserDtoMapper.toUserDto(user4));
        final ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                () -> userService.updateUser(returnUserSaved2.getId(), UserDtoMapper.toUserDto(user5)));
        Assertions.assertEquals("Пользователь с email = " + returnUserSaved1.getEmail() + " уже есть в коллекции",
                exception.getMessage());
    }
}
