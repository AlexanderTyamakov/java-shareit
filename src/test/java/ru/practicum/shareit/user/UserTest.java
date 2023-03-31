package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class UserTest {
    @Test
    public void testEquals() {
        User user1 = new User(12L, null, null);
        User user2 = new User(12L, null, null);
        assertEquals(user1, user2);
    }

    @Test
    public void testHashCode() {
        User user1 = new User(12L, null, null);
        User user2 = new User(12L, null, null);
        assertEquals(user1.hashCode(), user2.hashCode());
    }
}
