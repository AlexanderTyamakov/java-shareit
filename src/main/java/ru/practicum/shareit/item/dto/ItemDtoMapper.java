package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.item.Item;

public class ItemDtoMapper {
    public static ItemDto toItemDto(Item item) {
        return new ItemDto(
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getRequest() != null ? item.getRequest() : null
        );
    }

    public static Item toItem(ItemDto itemDto, long id, long owner) {
        return new Item(
                id,
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable(),
                owner,
                null
        );
    }

    public static Item patchToItem(ItemDtoPatch itemDtoPatch, Item item, long id, long owner) {
        return new Item(
                id,
                (itemDtoPatch.getName() != null && !itemDtoPatch.getName().isEmpty()) ? itemDtoPatch.getName() : item.getName(),
                (itemDtoPatch.getDescription() != null && !itemDtoPatch.getDescription().isEmpty()) ? itemDtoPatch.getDescription() : item.getDescription(),
                itemDtoPatch.getAvailable() != null ? itemDtoPatch.getAvailable() : item.getAvailable(),
                owner,
                null
        );
    }
}
