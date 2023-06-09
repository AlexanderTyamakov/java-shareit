package ru.practicum.shareit.requests;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@SpringBootTest
public class ItemRequestTest {
    @Test
    public void testEquals() {
        ItemRequest itemRequest1 = new ItemRequest(12L, null, null, null);
        ItemRequest itemRequest2 = new ItemRequest(12L, null, null, null);
        ItemRequest itemRequest3 = new ItemRequest(13L, null, null, null);
        ItemRequest itemRequest4 = new ItemRequest(null, null, null, null);
        User user1 = new User(12L, null, null);

        assertEquals(itemRequest1, itemRequest2);
        assertNotEquals(itemRequest1, user1);
        assertNotEquals(itemRequest1, itemRequest3);
        assertNotEquals(itemRequest1, itemRequest4);
    }

    @Test
    public void testHashCode() {
        ItemRequest itemRequest1 = new ItemRequest(12L, null, null, null);
        ItemRequest itemRequest2 = new ItemRequest(12L, null, null, null);
        assertEquals(itemRequest1.hashCode(), itemRequest2.hashCode());
    }
}
