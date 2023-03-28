package ru.practicum.shareit.item;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.dto.CommentDtoIn;
import ru.practicum.shareit.item.dto.CommentDtoOut;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {

    List<ItemDto> getAllItemsOfUser(long userId, Integer from, Integer size);

    ItemDto getItemById(long userId, long itemId);

    ItemDto saveItem(long userId, ItemDto itemDto);

    @Transactional
    ItemDto updateItem(long userId, ItemDto itemDto, long itemId);

    @Transactional
    List<ItemDto> searchItem(long userId, String text, Integer from, Integer size);

    @Transactional
    CommentDtoOut addComment(long userId, CommentDtoIn commentDtoIn, long itemId);
}
