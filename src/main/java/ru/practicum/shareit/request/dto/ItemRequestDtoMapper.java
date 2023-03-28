package ru.practicum.shareit.request.dto;

import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDtoMapper;

import java.time.LocalDateTime;
import java.util.List;

public class ItemRequestDtoMapper {

    public static ItemRequest toItemRequest (ItemRequestDtoIn itemRequestDtoIn, User user, LocalDateTime localDateTime) {
        return new ItemRequest(
                0L,
                itemRequestDtoIn.getDescription(),
                user,
                localDateTime
        );
    }

    public static ItemRequestDtoOut toItemRequestOut (ItemRequest itemRequest, List<Item> items) {
        return new ItemRequestDtoOut(
                itemRequest.getId(),
                itemRequest.getDescription(),
                UserDtoMapper.toUserDto(itemRequest.getRequestor()),
                itemRequest.getCreated(),
                items
        );
    }
}
