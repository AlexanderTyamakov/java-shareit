package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class UserDtoMapperTest {

    @Test
    public void testPatchToUser() {
        User user = new User(1L, "Jack", "jack@example.com");
        UserDto userDto1 = new UserDto(1L, "Smack", "smack@smack.com");
        UserDto userDto2 = new UserDto(1L, "John", null);
        UserDto userDto3 = new UserDto(1L, null, "mike@mike.com");
        User patchedUser = UserDtoMapper.patchToUser(userDto1, user, userDto1.getId());
        assertEquals(userDto1.getName(), patchedUser.getName());
        assertEquals(userDto1.getEmail(), patchedUser.getEmail());

        User patchedUser2 = UserDtoMapper.patchToUser(userDto2, user, userDto2.getId());
        assertEquals(userDto2.getName(), patchedUser2.getName());
        assertEquals(user.getEmail(), patchedUser2.getEmail());

        User patchedUser3 = UserDtoMapper.patchToUser(userDto3, user, userDto3.getId());
        assertEquals(user.getName(), patchedUser3.getName());
        assertEquals(userDto3.getEmail(), patchedUser3.getEmail());
    }
}
