package ru.practicum.shareit.utils;

import lombok.Getter;
import ru.practicum.shareit.exception.ValidationException;

@Getter
public class Pagination {
    private Integer pageSize;
    private Integer index;
    private Integer totalPages;

    public Pagination(Integer from, Integer size) {
        if (from < 0) {
            throw new ValidationException("Значение номера первой записи не может быть меньше нуля!");
        }
        if (size < 0) {
            throw new ValidationException("Значение размера страницы не может быть меньше нуля!");
        }
        if (size.equals(0)) {
            throw new ValidationException("Значение размера страницы должно быть больше нуля!");
        }
        pageSize = from;
        index = 1;
        if (from.equals(size)) {
            pageSize = size;
        }
        if (from.equals(0)) {
            pageSize = size;
            index = 0;
        }
        totalPages = index + 1;
        if ((from < size) && (!from.equals(0))) {
            totalPages = size / from + index;
            if (size % from != 0) {
                totalPages++;
            }
        }
    }
}