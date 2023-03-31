package ru.practicum.shareit.booking;


import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class BookingTest {

    @Test
    public void testEquals() {
        Booking booking1 = new Booking(123L,null,null,null,null,null);
        Booking booking2 = new Booking(123L,null,null,null,null,null);
        assertEquals(booking1, booking2);
    }

    @Test
    public void testHashCode() {
        Booking booking1 = new Booking(223L,null,null,null,null,null);
        Booking booking2 = new Booking(223L,null,null,null,null,null);
        assertEquals(booking1.hashCode(), booking2.hashCode());
    }
}
