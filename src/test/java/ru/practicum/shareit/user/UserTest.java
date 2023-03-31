package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.item.Item;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@SpringBootTest
public class UserTest {
    @Test
    public void testEquals() {
        User user1 = new User(12L, null, null);
        User user2 = new User(12L, null, null);
        User user3 = new User(13L, null, null);
        User user4 = new User(null, null, null);
        Item item1 = new Item(12L, null, null, null, null, null);
        assertEquals(user1, user2);
        assertNotEquals(user1, item1);
        assertNotEquals(user1, user3);
        assertNotEquals(user1, user4);
    }

    @Test
    public void testHashCode() {
        User user1 = new User(12L, null, null);
        User user2 = new User(12L, null, null);
        assertEquals(user1.hashCode(), user2.hashCode());
    }
}
