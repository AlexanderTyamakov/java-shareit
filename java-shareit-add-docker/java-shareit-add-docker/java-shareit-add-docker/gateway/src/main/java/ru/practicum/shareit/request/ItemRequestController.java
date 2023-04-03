package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDtoGate;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
public class ItemRequestController {

    private final ItemRequestClient itemRequestClient;

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getItemRequestById(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable("requestId") Long itemRequestId) {
        log.info("Поступил запрос в API Gateway - getItemRequestById");
        return itemRequestClient.getItemRequestById(userId, itemRequestId);
    }

    @GetMapping
    public ResponseEntity<Object> getItemRequestsOfUser(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Поступил запрос в API Gateway - getItemRequestsOfUser");
        return itemRequestClient.getItemRequestsOfUser(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllItemRequests(@RequestHeader("X-Sharer-User-Id") long userId, @RequestParam(defaultValue = "0") Integer from,
                                                     @RequestParam(defaultValue = "10") Integer size) {
        log.info("Поступил запрос в API Gateway - getAllItemRequests");
        return itemRequestClient.getAllItemRequests(userId, from, size);
    }

    @PostMapping
    public ResponseEntity<Object> saveRequest(@RequestHeader("X-Sharer-User-Id") long userId, @RequestBody @Validated ItemRequestDtoGate itemRequestDtoIn) {
        log.info("Поступил запрос в API Gateway - saveRequest");
        return itemRequestClient.saveRequest(userId, itemRequestDtoIn);
    }
}
