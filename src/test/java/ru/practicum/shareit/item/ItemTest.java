package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.user.User;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@SpringBootTest
public class ItemTest {
    @Test
    public void testEquals() {
        Item item1 = new Item(12L, null, null, null, null, null);
        Item item2 = new Item(12L, null, null, null, null, null);
        Item item3 = new Item(13L, null, null, null, null, null);
        Item item4 = new Item(null, null, null, null, null, null);
        User user1 = new User(12L, null, null);
        assertEquals(item1, item2);
        assertNotEquals(item1, user1);
        assertNotEquals(item1, item3);
        assertNotEquals(item1, item4);
    }


    @Test
    public void testHashCode() {
        Item item1 = new Item(12L, null, null, null, null, null);
        Item item2 = new Item(12L, null, null, null, null, null);
        assertEquals(item1.hashCode(), item2.hashCode());
    }
}