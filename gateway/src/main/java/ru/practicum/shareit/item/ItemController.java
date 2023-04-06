package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDtoGate;
import ru.practicum.shareit.item.dto.ItemDtoGate;
import ru.practicum.shareit.utils.Create;
import ru.practicum.shareit.utils.Update;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {

    private final ItemClient itemClient;

    @GetMapping
    public ResponseEntity<Object> getAllItems(@RequestHeader("X-Sharer-User-Id") long userId, @RequestParam(defaultValue = "0") Integer from,
                                              @RequestParam(defaultValue = "10") Integer size) {
        log.info("Поступил запрос в API Gateway - getAllItems");
        return itemClient.getAllItems(userId, from, size);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItem(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long itemId) {
        log.info("Поступил запрос в API Gateway - getItem");
        return itemClient.getItem(userId, itemId);
    }

    @PostMapping
    public ResponseEntity<Object> saveItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                           @RequestBody @Validated(Create.class) ItemDtoGate itemDto) {
        log.info("Поступил запрос в API Gateway - saveItem");
        return itemClient.saveItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                             @RequestBody @Validated(Update.class) ItemDtoGate itemDto, @PathVariable Long itemId) {
        log.info("Поступил запрос в API Gateway - updateItem");
        return itemClient.updateItem(userId, itemDto, itemId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                             @RequestParam String text,
                                             @RequestParam(defaultValue = "0") Integer from,
                                             @RequestParam(defaultValue = "10") Integer size) {
        log.info("Поступил запрос в API Gateway - searchItem");
        return itemClient.searchItem(userId, text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestHeader("X-Sharer-User-Id") long userId,
                                             @RequestBody @Validated CommentDtoGate commentDtoIn,
                                             @PathVariable Long itemId) {
        log.info("Поступил запрос в API Gateway - addComment");
        return itemClient.addComment(userId, commentDtoIn, itemId);
    }

}
