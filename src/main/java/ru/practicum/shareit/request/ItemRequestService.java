package ru.practicum.shareit.request;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.request.dto.ItemRequestDtoIn;
import ru.practicum.shareit.request.dto.ItemRequestDtoOut;

import java.time.LocalDateTime;
import java.util.List;

public interface ItemRequestService {

    ItemRequestDtoOut getItemRequestById(long userId, long itemRequestId);

    List<ItemRequestDtoOut> getItemRequestsOfUser(long userId);

    List<ItemRequestDtoOut> getAllItemRequests(long userId, int from, int size);

    @Transactional
    ItemRequestDtoIn saveRequest(long userId, ItemRequestDtoIn itemRequestDtoIn, LocalDateTime localDateTime);

}
