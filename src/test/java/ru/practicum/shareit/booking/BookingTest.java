package ru.practicum.shareit.booking;


import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.user.User;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@SpringBootTest
public class BookingTest {

    @Test
    public void testEquals() {
        Booking booking1 = new Booking(123L, null, null, null, null, null);
        Booking booking2 = new Booking(123L, null, null, null, null, null);
        Booking booking3 = new Booking(124L, null, null, null, null, null);
        Booking booking4 = new Booking(null, null, null, null, null, null);
        User user1 = new User(12L, null, null);
        assertEquals(booking1, booking2);
        assertNotEquals(booking1, user1);
        assertNotEquals(booking1, booking3);
        assertNotEquals(booking1, booking4);
    }

    @Test
    public void testHashCode() {
        Booking booking1 = new Booking(223L, null, null, null, null, null);
        Booking booking2 = new Booking(223L, null, null, null, null, null);
        assertEquals(booking1.hashCode(), booking2.hashCode());
    }
}
