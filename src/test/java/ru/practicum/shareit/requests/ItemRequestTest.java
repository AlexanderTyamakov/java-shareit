package ru.practicum.shareit.requests;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.request.ItemRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class ItemRequestTest {
    @Test
    public void testEquals() {
        ItemRequest itemRequest1 = new ItemRequest(12L, null, null, null);
        ItemRequest itemRequest2 = new ItemRequest(12L, null, null, null);
        assertEquals(itemRequest1, itemRequest2);
    }

    @Test
    public void testHashCode() {
        ItemRequest itemRequest1 = new ItemRequest(12L, null, null, null);
        ItemRequest itemRequest2 = new ItemRequest(12L, null, null, null);
        assertEquals(itemRequest1.hashCode(), itemRequest2.hashCode());
    }
}
