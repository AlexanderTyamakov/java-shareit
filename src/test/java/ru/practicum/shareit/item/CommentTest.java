package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class CommentTest {
    @Test
    public void testEquals() {
        Comment comment1 = new Comment(12L, null, null, null, null);
        Comment comment2 = new Comment(12L, null, null, null, null);
        assertEquals(comment1, comment2);
    }

    @Test
    public void testHashCode() {
        Comment comment1 = new Comment(12L, null, null, null, null);
        Comment comment2 = new Comment(12L, null, null, null, null);
        assertEquals(comment1.hashCode(), comment2.hashCode());
    }
}