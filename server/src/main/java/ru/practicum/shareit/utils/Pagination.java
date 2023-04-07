package ru.practicum.shareit.utils;

import lombok.Getter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

@Getter
public class Pagination extends PageRequest {
    private Integer from;


    public Pagination(int from, int size, Sort sort) {
        super(from / size, size, sort);
        this.from = from;
    }

    @Override
    public long getOffset() {
        return this.from;
    }
}