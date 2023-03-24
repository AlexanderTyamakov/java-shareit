package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

public class ItemDtoMapper {
    public static ItemDto toItemDto(Item item) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getRequest() != null ? item.getRequest() : null
        );
    }

    public static Item toItem(ItemDto itemDto, long owner) {
        return new Item(
                itemDto.getId(),
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable(),
                owner,
                null
        );
    }

    public static Item patchToItem(ItemDto itemDto, Item item, long id) {
        return new Item(
                id,
                (itemDto.getName() != null && !itemDto.getName().isEmpty()) ? itemDto.getName() : item.getName(),
                (itemDto.getDescription() != null && !itemDto.getDescription().isEmpty()) ? itemDto.getDescription() : item.getDescription(),
                itemDto.getAvailable() != null ? itemDto.getAvailable() : item.getAvailable(),
                null,
                null
        );
    }
}
