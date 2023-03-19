package ru.practicum.shareit.item.dto;

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
public class ItemDtoPatch {
    private final String name;
    private final String description;
    private final Boolean available;
    private final Long request;
}
