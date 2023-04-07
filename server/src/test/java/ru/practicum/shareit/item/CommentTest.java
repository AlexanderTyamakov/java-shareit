package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.user.User;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@SpringBootTest
public class CommentTest {
    @Test
    public void testEquals() {
        Comment comment1 = new Comment(12L, null, null, null, null);
        Comment comment2 = new Comment(12L, null, null, null, null);
        Comment comment3 = new Comment(13L, null, null, null, null);
        Comment comment4 = new Comment(null, null, null, null, null);
        User user1 = new User(12L, null, null);
        assertEquals(comment1, comment2);
        assertNotEquals(comment1, user1);
        assertNotEquals(comment1, comment3);
        assertNotEquals(comment1, comment4);
    }

    @Test
    public void testHashCode() {
        Comment comment1 = new Comment(12L, null, null, null, null);
        Comment comment2 = new Comment(12L, null, null, null, null);
        assertEquals(comment1.hashCode(), comment2.hashCode());
    }
}