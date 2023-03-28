package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.booking.dto.LastBookingDto;
import ru.practicum.shareit.booking.dto.NextBookingDto;
import ru.practicum.shareit.item.Comment;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

public class ItemDtoMapper {
    public static ItemDto toItemDto(Item item, LastBookingDto lastBookingDto, NextBookingDto nextBookingDto, List<CommentDtoOut> commentDtoOut) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getRequest() != null ? item.getRequest() : null,
                lastBookingDto,
                nextBookingDto,
                commentDtoOut
        );
    }

    public static ItemDto toItemDto(Item item, List<CommentDtoOut> commentDtoOut) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getRequest() != null ? item.getRequest() : null,
                null,
                null,
                commentDtoOut
        );
    }

    public static ItemDtoShort toItemDtoShort(Item item) {
        return new ItemDtoShort(
                item.getId(),
                item.getName()
        );
    }

    public static Item toItem(ItemDto itemDto, long owner) {
        return new Item(
                itemDto.getId(),
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable(),
                owner,
                itemDto.getRequest()
        );
    }


    public static Item patchToItem(ItemDto itemDto, Item item, long id) {
        return new Item(
                id,
                (itemDto.getName() != null && !itemDto.getName().isEmpty()) ? itemDto.getName() : item.getName(),
                (itemDto.getDescription() != null && !itemDto.getDescription().isEmpty()) ? itemDto.getDescription() : item.getDescription(),
                itemDto.getAvailable() != null ? itemDto.getAvailable() : item.getAvailable(),
                null,
                itemDto.getRequest() != null ? itemDto.getRequest() : item.getRequest()
        );
    }

    public static Comment toComment(CommentDtoIn commentDtoIn, Item item, User author, LocalDateTime created) {
        return new Comment(
                0L,
                commentDtoIn.getText(),
                item,
                author,
                created
        );
    }

    public static CommentDtoOut toCommentDtoOut(Comment comment, String author) {
        return new CommentDtoOut(
                comment.getId(),
                comment.getText(),
                author,
                comment.getCreated()
        );
    }
}
