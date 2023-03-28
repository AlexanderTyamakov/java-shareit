package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.ErrorResponse;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestDtoIn;
import ru.practicum.shareit.request.dto.ItemRequestDtoOut;

import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    @GetMapping("/{requestId}")
    public ItemRequestDtoOut getItemRequestById(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable("requestId") Long itemRequestId) {
        return itemRequestService.getItemRequestById(userId, itemRequestId);
    }

    @GetMapping
    public List<ItemRequestDtoOut> getItemRequestsOfUser(@RequestHeader("X-Sharer-User-Id") long userId) {
        return itemRequestService.getItemRequestsOfUser(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDtoOut> getAllItemRequests(@RequestHeader("X-Sharer-User-Id") long userId, @RequestParam(defaultValue = "0") Integer from,
                                                     @RequestParam(required = false) Integer size) {
        return itemRequestService.getAllItemRequests(userId, from, size);
    }

    @PostMapping
    public ItemRequestDtoOut saveRequest(@RequestHeader("X-Sharer-User-Id") long userId, @RequestBody @Validated ItemRequestDtoIn itemRequestDtoIn) {
        return itemRequestService.saveRequest(userId, itemRequestDtoIn, LocalDateTime.now());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handle(final ValidationException e) {
        return new ErrorResponse(
                "Ошибка в отправленных данных", e.getMessage()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handle(final NotFoundException e) {
        return new ErrorResponse(
                "Отсутствует объект", e.getMessage()
        );
    }
}
