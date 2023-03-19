package ru.practicum.shareit.item;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * TODO Sprint add-controllers.
 */
@Getter
@Setter
@RequiredArgsConstructor
@ToString
public class Item {
    private final Long id;
    private final String name;
    private final String description;
    private final Boolean available;
    private final Long owner;
    private final Long request;
}
