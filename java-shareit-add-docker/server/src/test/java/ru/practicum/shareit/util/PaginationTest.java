package ru.practicum.shareit.util;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.utils.Pagination;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class PaginationTest {

    private Sort sort;

    @Test
    public void whenSizeNotPositiveThenValidateException() {
        int from = 0;
        int size = -1;
        assertThrows(IllegalArgumentException.class, () -> new Pagination(from, size, sort));
    }

    @Test
    public void whenFromNotPositiveThenValidateException() {
        int from = -1;
        int size = 1;
        assertThrows(IllegalArgumentException.class, () -> new Pagination(from, size, sort));
    }

    @Test
    public void whenSizeLessOneThenValidateException() {
        int from = 0;
        int size = 0;
        assertThrows(ArithmeticException.class, () -> new Pagination(from, size, sort));
    }

    @Test
    public void whenSizeLessOneAndFromPositiveValidateException() {
        int from = 1;
        int size = 0;
        assertThrows(ArithmeticException.class, () -> new Pagination(from, size, sort));
    }
}
